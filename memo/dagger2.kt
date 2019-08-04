/*----------------------------------------------------------------------------------
                                        Dagger2
 ----------------------------------------------------------------------------------
 https://qiita.com/ko2ic/items/35d077499734bfd777fc

 [dependency injection（DI）]
 ・外部から注入（injection）する手法
    ・テストなど、状況に応じて注入するインスタンスを切替えられる
    ・インスタンス化の方法が変わっても利用側は影響をうけない

[@Component]
 ・injectorのこと
 ・inject される側（Activity）は、Component を経由して必要なインスタンを注入してもらう
 ・@Component つけて、abstractクラス、 interface で定義
 ・modules 属性に必要な Moduleクラスを指定
 ・インターフェース型の変数に、インジェクトをしたい場合
   Moduleを切替えることで、生成するインスタンスを指定できる
 ・ビルドすると Component の実装クラスが自動生成される。名前は「 Dagger + Componentクラス名」

    @Component(modules = ApiModule.class)
    interface AppComponent {
        var apiService():ApiService
    }



 ・Component のインスタンスを生成するには、自動生成された Component実装クラス.Builder で生成
    val appComponent = DaggerAppComponent.builder()
                    .appModule(AppModule(this))
                    .modelModule(ModelModule())
                    .build()

・inject()のタイミングで、@Injectを指定しているフィールドに対して、インスタンスが挿入される

（2.11から）
・指定したActivityに、Componentを自動生成するModuleが作れるようになった
・@ContributesAndroidInjector で、インジェクションして欲しいActivityを指定した@Moduleを作成し
　＠Componentにセットしておけば、そのActivityで、@Componentのインスタンスを裏で自動生成してくれる

    // 1.指定したActivityにComponentを自動生成するModule作成
    @Module
    abstract class AndroidModule {

        @ContributesAndroidInjector
        abstract fun contributeMainActivity(): MainActivity     // Componentを生成したいActivity
    }

    // 2.Componentに、@Module をセット
    @Component(modules = [AndroidModule::class])
    interface AppComponent {...}

    // 3.Applicationクラスで、HasActivityInjectorインターフェースを実装
    class App : Application(), HasActivityInjector {

    // 4.ActivityのonCreateで、inject
    AndroidInjection.inject(this)

    AndroidInjection.inject(this) が具体的に何をしているかというと、
    Applicationクラスで、HasActivityInjectorが実装されていれば、
    DispatchingAndroidInjectorを使って引数のActivityを
    Component(これは依存を解決するためのクラス。別名ObjectGraph。)に入れています。
    そして、Activityで@InjectしているフィールドにInjectしてくれます

[@Module]
 ・責務はインスタンスの生成
 ・inject するクラスを返すメソッドを定義（@Provideメソッド）
    [@Provides]
    ・@Providesをつける
    ・名前は「Provide + 生成するクラス名」
    ・戻り値に生成したいクラスを指定

        「@Moduleクラス名 _ @Provideメソッド名 + Factory」
        ・裏でFactory が自動生成される。
        ・Component は、Factory を介して Module の provideメソッド を呼出し、インスタンスを生成している


    @Module
    class AppModule(private val application: Application) {

        @Singleton                      // 何度コールしても同じSessionManagerインスタンスが挿入される
        @Provides
        fun provideSessionManager(): SessionManager {
            return SessionManager()
        }
    }




[Injectされるクラス]
・コンストラクタに @Inject をつける
・Daggerは、クラスをインスタンス化する時に @Injectを使っているコンストラクタを使う
・@Injectがついたコンストラクタがない場合、Daggerはインスタンスは作成しない。


[Activity、Fragment]
・挿入したいオブジェクトに、@Injectをつける
 ・inject()のタイミングで、@Injectを指定しているフィールドに対して、インスタンスが挿入される

    class MyViewModel @Inject constructor(private val mPresenter: PersonPresenter)

     @Inject lateinit var personPresenter: PersonPresenter
 ・
[]
 ・
 ・
[]
 ・
 ・
 ----------------------------------------------------------------------------------
 [seedInstance]
 AndroidInjectorのバインディンググラフで使用されるインスタンスを提供
 ----------------------------------------------------------------------------------
 [AndroidInjector]
 (ActivityまたはFragment）のメンバーインジェクションを実行。
 dagger.Subcomponentアノテーションによって実装されている

 [dagger.Subcomponentアノテーション]
 dagger.Subcomponent.Builder extends AndroidInjector.Builder.
 ----------------------------------------------------------------------------------
 [Subcomponent]
 親のComponent からバインディングを継承するサブコンポーネント。
 ----------------------------------------------------------------------------------
 [@IntoMap]
 [@MapKey]
 マップを作成するため、@Providesメソッドによって返される値に、キーを付けるアノテーション
 @MapKeyは単一のメンバーを持ち、マップキーとして使用される。

    //@Mapkey として使う@アノテーションを定義（キーとして使うものを定義)
     @MapKey
     @interface SomeEnumKey {
      SomeEnum value();
    }


     @Module
    class SomeModule {
       //@Providesメソッド で返される値に、@アノテーションで key をつけて Map にする
       @Provides
       @IntoMap
       @SomeEnumKey(SomeEnum.FOO)
      fun provideFooValue():Integer {
        return 2                   // put(SomeEnum.FOO, 2)
      }
    }

    class SomeClass {
       @Inject
       var map: Map<SomeEnum, Integer>          //map.get(SomeEnum.FOO) == 2
    }

 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------
 []
 ----------------------------------------------------------------------------------*/
