# CleanAndroid
## Установка
Текущая версия
```
clean_android_version = '1.2.3'
```

Зависимости
```
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.indrih17:cleanandroid:$clean_android_version'
}
```


## Предисловие
Данная библиотека является аналогом Moxy и не призвана полностью её заменить. 
Эта библиотека задумывалась как более лёгкий аналог, с меньшим количеством 
возможностей и без кодогенерации.
Так же, тут из коробки поддерживается простой менеджмент жизненного цикла корутин.
Моё более подробное видение архитектуры можно найти в 
[CleanContract](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/CleanContract.kt).

## Идеология
Я придерживаюсь идеи программирования на уровне интерфейсов. 
По моим убеждениям, контракты должны быть описаны для каждого экрана.
Вам не обязательно делать всё то, что делаю я, чтобы успешно пользоваться CleanAndroid.

Но парочку своих принципов я всё же навязываю:
* После ухода с текущего экрана на другой экран все запущенные корутины 
должны быть остановлены. Если хотите бекграунд - сервисы Вам в руки. :)
* Корутины не должны запускаться в Activity/Fragment.
* Presenter должен получать зависимости, переданные с предыдущего экрана,
а не View.

## Необходимые подробности
* Presenter указывает View что необходимо отобразить с помощью 
[Event](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/AbstractEvent.kt).
* Стандартная навигация между экранами предполагает использование 
`Navigation Component`.
* Переход между экранами осуществляется передачей наследников 
[AbstractScreen](https://github.com/indrih17/cleanandroid/blob/master/cleanandroid/src/main/java/indrih/cleanandroid/AbstractScreen.kt).
* Если вам нужно запускать некоторый код при каждом attach/detach презентера, 
положите этот код в методы `attachView` и `detachView`.
Если же действия нужно совершить при первом поключении или в момент 
отчистки ресурсов, положите код в методы `onFirstAttached` и `onCleared`.
* Ваше Activity должно наследоваться от `CleanActivity`.
* Ваши Fragment-ы должны наследоваться от `CleanRetainFragment`.
* Ваши Presenter-ы должны наследоваться от `CleanPresenter`.

## Ответственность и возможности базовых сущностей
* `CleanRetainFragment` - базовый абстрактный Фрагмент, передающий 
`CleanPresenter`-у информацию о своём жизненном цикле. Так же имеет
базовые методы `showAlert`, `showToast` и т.д., которые выводят на экран
информацию и сами умеют правильно удалить выполненные Event.

   Внимание: по умолчанию флаг `isRetain = true`. Фрагмент будет 
   сам заботиться о том, чтобы сообщить `Presenter`-у о необходимости
   отчистить ресурсы. Если же Вы хотите чтобы определённый Фрагмент был
   не `retainInstance` - Вы можете в блоке `init` указать значение `false`,
   но учтите, что Вам самим придётся сохранять Presenter от пересоздания,
   а так же самим уведомлять об отчистке.

* `CleanPresenter` - базовый Presenter, который может:

   1) При подключении View либо выдавать уже созданные Event-ы 
   (если была смена ориентации), либо выполнять `onFirstAttached`.
   2) При команде отчистить ресурсы останавливает запущенные корутины
   и отчищает буфер событий.
   3) Самостоятельно следит за жизненным циклом Event-ов (только если Вы
   не укажите явно, что хотите делать это сами).
   4) Работает со скрытым Router-ом, передавая ему необходимые параметры
   при навигации между экранами.
   5) Получает зависимости от Router-а.

* `CleanInteractor` - очень тонкий абстрактный класс. Имеет лишь 
одну проперти `standardContext = Dispatchers.Default` и один 
экстеншен `def`, запускающий корутину в `Default` контексте и дожидающийся
её выполнения.

* `CleanGateway` - рассчитан на тех, кто держит кеш с данными в Gateway.
Имеет созданный `protected` объект 
[Mutex](https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html#mutual-exclusion),
а так же набор mutex-экстеншенов для `MutableCollection` и `MutableMap`.

   Примеры:
   ```
   userList.safeAdd(user)
   ```
   
   ```
   userList.safeFirstOrNull { it.id == 5 }
   ```
   
   ```
   someMap.safeForEach { suspendFunc(it.key, it.value) }
   ```

## Правильный менеджемент корутин
#### На стороне Презентера
По умолчанию запуск идёт в `Main`.
```
launch {
    val userList = interactor.selectAllUsers()
    notifyUI(SetOnDisplay(userList))
}
```

Так же есть функция `singleLaunch`: лишь одна такая функция может выполняться
одновременно.

#### На стороне Интерактора
По умолчанию запуск идёт в `Default`.
```
fun selectAllUsers() = def { 
    gateway.selectAllUsers()
} 
```

Или если Вам нужно запускать несколько параллельных корутин
```
fun selectAllUsers() = coroutineScope { 
    launch(standardContext) {
        // ...
    }
} 
```

## Events
#### На стороне контракта
Создаём базовый Event, который будет содержать общие команды для всех экранов:
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

#### На стороне Фрагмента
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
#### На стороне Презентера
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
    interactor.createUser(surname, name)
    notifyUI(SavingCompleted)
}
```

## Жизненный цикл событий
У функции `notifyUI` есть параметр `showMode`, который отвечает за то, 
сколько будут жить события.

1) Once (используется по умолчанию) - такие события должны быть удалены 
сразу после завершения того, зачем они были созданы. 
Если используются встроенные методы `showAlert`, `showKeyboard` и т.д., 
то события будут удалены когда информация будет доставлена до пользователя.

   Имеет флаг `autoRemoval` - если `true` (флаг по умолчанию),
   то после вывода на экран такие события будут удалены автоматически 
   (за исключением тех, что будут переданы в `showAlert` и т.д.). 
   Если Вы хотите вручную отчищать событие (это можно сделать с помощью
   `CleanPresenter.eventIsCommitted`) - установите значение `false`.

2) EveryTime - такие события будут воспроизводиться при каждом подключении View к Presenter
   и удалить их **нельзя**, можно лишь заменить аналогичным событием.
   Так же, они переживут и onCleared. Умрут они только вместе с Presenter-ом.
3) Chain - цепочка событий.
 
   Этот вариант имеет несколько нюансов:

   a) он может использоваться только с объектами.

   b) чем длиннее цепочка, тем больше шансов ошибиться и поставить не тот параметр в цепочку.

   c) Любая опечатка будет стоить Вам жизни. :)

   Но тем не менее, иногда этот вариант удобен. К примеру:
   у Вас есть два Event - ShowProgress и HideProgress:
   ```
   sealed class Event : AbstractEvent() {
       object ShowProgress : Event()
       
       object HideProgress : Event() 
   }
   ```

   Вы организовываете их в цепочку (Цепочка подобна связанным спискам):
   ```
   notifyUI(
       Event.ShowProgress,
       showMode = Chain(next = Event.HideProgress)
   )
   // ...
   notifyUI(
       Event.HideProgress,
       showMode = Chain(prev = Event.ShowProgress)
   )
   ```

   Сначала отправляется на отображение ShowProgress, пользователь
   поворачивает устройство, этот Event восстанавливается из буфера. 
   Как только приходит HideProgress - он идёт на отображение, а затем 
   они вместе с ShowProgress удаляются из буфера. 
   
   Цепочку можно организовать любой длины.
   
## Передача аргументов
1) Объявляем Screen для нужного экрана, передавая необходимые данные
в базовый `AbstractScreen`:
   ```
   sealed class Screen(action: Int, vararg pairs: Pair<String, Any>) : AbstractScreen(action, *pairs) {
      // без параметров
      object DeviceList : Screen(
          R.id.action_deviceAddFragment_to_deviceListFragment
      )
   
      // с параметрами
      class StmParamsAdd(device: Device) : Screen(
          R.id.action_deviceAddFragment_to_stmParamsAddFragment,
          "device" to device
      )
   }
   ```
   
2) Когда создаём Presenter для данного экрана, передаём в `CleanPresenter`
`sealed`-класс в виде дженерика.

3) Для перехода на нужный экран в Presenter-е вызываем:
   ```
   // без параметров
   navigateTo(DeviceList)
   
   // с параметрами
   navigateTo(StmParamsEdit(device))
   ```
   
4) Для получения переданных зависимостей в Presenter-е:
   ```
   val device: Device = getArg()
   ```
   
   Если же Вы передаёте несколько зависимостей одинакового типа:
   ```
   val device1: Device = getArg("device1")
   val device2: Device = getArg("device2")
   ```
   
   Но можно получить все аргументы без приведений типов
   ```
   val args: HashMap<String, Any> = getAllArgs()
   ```
