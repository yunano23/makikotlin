/*----------------------------------------------------------------------------------
                                        Dagger2
----------------------------------------------------------------------------------
https://qiita.com/ko2ic/items/35d077499734bfd777fc

[dependency injection（DI）]
・外部から注入（injection）する手法
・テストなど、状況に応じて注入するインスタンスを切替えられる
・インスタンス化の方法が変わっても利用側は影響をうけない
----------------------------------------------------------------------------------
[@Component]
・injectorのこと
・inject される側（Activity）は、Component を経由して必要なインスタンを注入してもらう
・@Component つけて、abstractクラス、 interface で定義
・modules 属性に必要な Moduleクラスを指定
・インターフェース型の変数に、インジェクトする場合、Moduleで、生成するインスタンスを指定して切替できる
・ビルドすると Component の実装クラスが自動生成される。名前は「 Dagger + Componentクラス名」

1.@Component の抽象クラスを定義
@Component(modules = ApiModule.class)
interface AppComponent {
    var apiService():ApiService
}



2.利用側で、@Component をインスタンス化
val appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .modelModule(ModelModule())
                .build()

3.利用側で、@Component.inject() で
　@Injectを指定しているフィールドに対して、インスタンスが挿入

AndroidInjection（2.11から）-------------------------------------------------
・指定したActivityに、@Componentを自動生成するModuleが作れるようになった
    1.@ContributesAndroidInjector で、インジェクションして欲しいActivityを指定した@Moduleを作成
    2.＠Componentにセットしておく
    3.Axtivityでは、AndroidInjection.inject(this)するだけ
        そのActivityで、@Componentのインスタンスを裏で自動生成してくれる

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
DispatchingAndroidInjector を使って引数のActivityを
Component(これは依存を解決するためのクラス。別名ObjectGraph。)に入れ
Activityで @InjectをつけたフィールドにInjectしてくれます


----------------------------------------------------------------------------------
[@Module]
・責務はインスタンスの生成
・@Provideメソッドを定義（inject するクラスを返すメソッド）
[@Provides]
・名前は「Provide + 生成するクラス名」
・戻り値に生成したいクラスを指定
[@Binds]
・@Providesの代わりに利用
・class と @Provideメソッド は abstract にすること

@Module
class AppModule(val application: Application) {

    @Singleton               // 何度コールしても同じSessionManagerインスタンスが挿入される
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }
}


・裏でFactory が自動生成される
    ・名前は「@Moduleクラス名 _ @Provideメソッド名 + Factory」
    ・Component は、Factory を介して Module の provideメソッド を呼出し、インスタンスを生成している



----------------------------------------------------------------------------------
[@Injectでインスタンス化されるクラス]
・コンストラクタに @Inject をつける
・Daggerは、クラスをインスタンス化する時に @Injectを使っているコンストラクタを使う
・@Injectがついたコンストラクタがない場合、Daggerはインスタンスは作成できない。

class MyViewModel @Inject constructor(){}


----------------------------------------------------------------------------------
[Activity、Fragment（利用側）]
・挿入したいオブジェクトに、@Injectをつける
・inject()のタイミングで、@Injectを指定しているフィールドに対して、インスタンスが挿入される

 @Inject
 lateinit var personPresenter: PersonPresenter

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


/*----------------------------------------------------------------------------------
                                AndroidInjector
----------------------------------------------------------------------------------
[AndroidInjector]
Androidタイプ(ActivityやFragment）に、メンバーインジェクションを実行。
@Subcomponentアノテーションによって実装されている
----------------------------------------------------------------------------------
[@Subcomponent]
dagger.Subcomponent.Builder extends AndroidInjector.Builder.
親の Component からバインディングを継承するサブコンポーネント。
----------------------------------------------------------------------------------
[seedInstance]
AndroidInjector のバインディンググラフで使用されるインスタンスを提供
----------------------------------------------------------------------------------

----------------------------------------------------------------------------------
[DispatchingAndroidInjector]
Androidタイプ（Activity、Fragmentなど）のインスタンスに、メンバーインジェクションを実行
----------------------------------------------------------------------------------
[HasAndroidInjectorインターフェース]
AndroidInjector を返す #androidInjector() をもつ
    fun androidInjector():AndroidInjector<Object>

[HasAndroidInjectorインターフェース のサブクラス]
それぞれAndroidタイプのInjectorを返すメソッドが定義されている
    HasActivityInjector.activityInjector():DispatchingAndroidInjector<Activity>
    HasFragmentInjector.fragmentInjector():DispatchingAndroidInjector<Fragment>
----------------------------------------------------------------------------------
[@BindsInstance ]
インスタンスを Component 内のキーにバインドするようにするため
Component.Builderのメソッドや Component.Factoryのパラメーターをマークするのに使う

ビルダーやファクトリのクライアントが、 これらのインスタンスがコンポーネント内に渡して注入できるようにする
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
/*----------------------------------------------------------------------------------
                        DaggerAppCompatActivity
----------------------------------------------------------------------------------
[DaggerAppCompatActivity]
onCreateのタイミングで、　AndroidInjection.inject(this)　する

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }
----------------------------------------------------------------------------------
[DaggerApplication]
・HasActivityInjector、HasFragmentInjectorインターフェースなどをimplementsしている
    class DaggerApplication extends Application
        implements HasActivityInjector, HasFragmentInjector,

・DispatchingAndroidInjectorが、メンバーインジェクションされている
    @Inject DispatchingAndroidInjector<Activity> activityInjector
    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector

・HasActivityInjector.activityInjector()で、injectされたメンバーを返すようになってる
      @Override
      public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityInjector;
      }

 [追加で必要な実装]
 ・applicationInjectorを返すメソッドを実装すること
     override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
         return DaggerAppComponent.builder().create(this)
     }
----------------------------------------------------------------------------------
[]
----------------------------------------------------------------------------------
[]
----------------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------------
                                 @Binds
----------------------------------------------------------------------------------
[違い]
・@Provides の代わりに @Binds
・class と Providesメソッド は、abstract であること
・同一Module内に @Providesメソッド を定義できない
----------------------------------------------------------------------------------
@Provides は、Provideメソッド毎に裏で Factoryクラスをたくさんつくるが、
@Binds は、Factoryを作らないのでコードレス化できるのでこれからはコッチが推奨

abstract fun provideクラス名( some:実装クラス型):インターフェース型
----------------------------------------------------------------------------------*/
 interface IRepository {
     fun fetch():Item
}

 class RepositoryImpl : IRepository {
    override fun fetch() :Item{
        // 何らかの実装
    }
}

[@Bindsでない場合]
@Module
class Module {
    @Provides
    fun provideRepository( repository:RepositoryImpl) :IRepository{
        return repository;
    }
}
--------------------------------------------------------------------
[@Bindsの場合]
@Module
abstract class Module {
    @Binds
    abstract  provideRepository(RepositoryImpl repository):IRepository
}





/*----------------------------------------------------------------------------------
                               @IntoMap / @MapKey
----------------------------------------------------------------------------------
[@IntoMap]

----------------------------------------------------------------------------------

[@MapKey]
Mapを作成するため、@Providesメソッドによって返される値に、キーを付けるアノテーション
@MapKeyは単一のメンバーを持ち、マップキーとして使用される。
----------------------------------------------------------------------------------
[]
----------------------------------------------------------------------------------
[]
----------------------------------------------------------------------------------*/
 //@MapKey として使う@アノテーションを定義（キーとして使うものを定義)
  @MapKey
  @interface SomeEnumKey {
    val value:SomeEnum
 }


 @Module
 class SomeModule {
   //@Providesメソッド で返される値に、@アノテーションで key をつけて Map にする
   @Provides
   @IntoMap
   @SomeEnumKey(SomeEnum.FOO)
   fun provideFooValue():Int {
     return 2                   // put(SomeEnum.FOO, 2)
   }
 }

 class SomeClass {
    @Inject
    var map: Map<SomeEnum, Integer>          //map.get(SomeEnum.FOO) == 2
 }
