# CleanAndroid
## Предисловие
Данная библиотека является аналогом Moxy и не призвана полностью её заменить. 
Эта библиотека задумывалась как более лёгкий аналог, с меньшим количеством возможностей и без кодогенерации.
Так же, тут из коробки поддерживается простой менеджмент жизненного цикла корутин.
Моё более подробное видение архитектуры можно найти в [CleanContract](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/CleanContract.kt).

## Идеология
Я придерживаюсь идеи программирования на уровне интерфейсов. По моим убеждениям, контракты должны быть описаны для каждого экрана.
Вам не обязательно делать всё то, что делаю я, чтобы успешно пользоваться CleanAndroid.

Но парочку своих принципов я всё же навязываю:
* Навигацией между экранами должна заниматься отдельная сущность (Router). Не View и не Presenter. Presenter лишь может командовать роутером.
* После ухода с текущего экрана на другой экран все запущенные корутины должны быть остановлены. Если хотите бекграунд - сервисы Вам в руки. :)
* Корутины не должны запускаться в Activity/Fragment.

## Необходимые подробности
* По умолчанию базовый фрагмент - retain. 
* Presenter указывает View что необходимо отобразить с помощью [Event](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/AbstractEvent.kt).
* Если вам нужно запускать некоторый код при каждом attach/detach презентера, положите этот код в методы `attachView` и `detachView`.
Если же действия нужно совершить при первом поключении или в момент отчистки ресурсов, положите код в методы `onFirstAttached` и `onCleared`.

## Примеры кода
### На стороне контракта
Создаём Event, который будет содержать общие команды для всех экранов:
```
sealed class MainEvent : CleanContract.AbstractEvent() {
    object ShowKeyboard : MainEvent()

    object HideKeyboard : MainEvent()
}
```

Определяем нужный нам Event:
```
sealed class Event : CleanContract.AbstractEvent() {
    class SetUserInfo(val user: User) : Event()
    
    object ShowProgressBar : Event()
    
    object HideProgressBar : Event()
    
    class Main<M : MainEvent>(val main: M) : Event() // обёртка для MainEvent
}
```

### На стороне Фрагмента
Добавляем в свой главный абстрактный Фрагмент обработку общих событий:
```
fun notifyMain(event: Event, mainEvent: MainEvent) {
    when (mainEvent) {
        is ShowKeyboard ->
            showKeyboard(event)

        is HideKeyboard ->
            hideKeyboard(event)

        else -> {} // добавлен как индикатор того, для всех ли событий была прописана реализация
                   // если IDE подчёркивает серым - значит всё верно. :)
    }
}
```

Переопределяем метод `fun notify(event: Event)` в дочернем Фрагменте:
```
override fun notify(event: Event) {
    when (event) {
        is SetUserInfo ->
            // ...

        is ShowProgressBar ->
            // ...

        is HideProgressBar -> 
            // ...
            
        is Main<*> ->
            notifyMain(event, event.main)

        else -> {}
    }
}
```
### На стороне Презентера
* Пример обычного использования
```
launch {
  notifyUI(ShowProgressBar)
  val user = interactor.loadUserInfo()
  notifyUI(HideProgressBar)
  notifyUI(SetUserInfo(user))
}
```

* Пример с использованием `MainEvent`
```
val event = when("") {
    surname -> 
        EnterSurname
    name -> 
        EnterName
    else -> 
        null
}
if (event != null) {
    notifyUI(Main(ShowKeyboard))
    notifyUI(event) 
} else {
    createUser(surname, name)
    notifyUI(SavingCompleted)
}
```

## Отменяемость и самоуничтожаемость событий
Некоторые Event-ы нужно обработать лишь раз: например, установка параметров.
Пользователь может поменять эти параметры, а затем повернёт телефон - и если Event с параметрами 
не будет отменён - он автоматически заменит новые данные на старые.
Что же делать? Отменять Event после того как он выполнил своё назначение.

Как? Если несколько вариантов:

1) [CleanPresenter.eventIsCommitted](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/CleanPresenter.kt#L68) 
- вы можете в любой удобный для вас момент удалить событие.
2) [AbstractEvent.isOneTime](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/AbstractEvent.kt#L42) 
- при описании объекта/класса, реализующего некоторый Event, 
установить этот параметр true - тогда событие не будет сохраняться в буфер.
```
sealed class Event : AbstractEvent() {
    object Foo : Event() {
        init { isOneTime = true }
    }
}
```

3) Есть ещё вариант организовать цепочку событий 
[AbstractEvent.next & prev](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/AbstractEvent.kt#L16)
. Но этот вариант имеет несколько нюансов:

   a) он может использоваться только с объектами.

   b) чем длиннее цепочка, тем больше шансов ошибиться и поставить не тот параметр в цепочку.

   Но тем не менее, иногда этот вариант удобен, к примеру:
   у вас есть два Event - ShowProgress и HideProgress. Вы организовываете их в цепочку
   (Цепочка подобна связанным спискам):
   ```
   sealed class Event : AbstractEvent() {
       object ShowProgress : Event() {
           init { next = HideProgress }
       }
       object HideProgress : Event() {
           init { prev = ShowProgress }
       }
   }
   ```

   Сначала вы отправляете на отображение ShowProgress, пользователь поворачивает устройство,
   этот Event восстанавливается из буфера. Как только приходит HideProgress - он идёт на отображение, 
   а затем они вместе с ShowProgress удаляются из буфера. Цепочку можно организовать любой длины.