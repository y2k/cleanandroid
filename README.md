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
