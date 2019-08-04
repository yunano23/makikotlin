package com.example.yamamoto.android_maki.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yamamoto.android_maki.R


/*----------------------------------------------------
                   @アノテーションクラス
 ----------------------------------------------------
 @アノテーションクラスを定義 「@interface アノテーションクラス名」
 @アノテーション をつけると内部に @アノテーションクラス のインスタンスが生成される
 @アノテーションクラス のインスタンスにアクセスしたい場合は、リフレクションを利用する

----------------------------------------------------
//アノテーションクラス定義

@Retention(アノテーションを保持する範囲)
@Target(アノテーションを適用する要素)
public @interface アノテーション名{
}

@Target             アノテーションを付けることができる要素の種類（クラス、関数、プロパティ、式など）
@Retention          アノテーションをコンパイルされたクラスファイルに含めるかと、実行時にリフレクションを通して可視化するかどうか。デフォルトはどちらも true。
@Repetable          １つのエレメントに同じアノテーションを複数回使うか。
@MustBeDocumented   アノテーションを公開APIの一部とするか、生成されるAPIドキュメントに表示されるクラスやメソッドのシグネチャに含めるかどうか

public @interface TestAnnotation1 {
String version() default "1";
String author();
}

----------------------------------------------------
1.アノテーションをつける
内部にアノテーションクラスのインスタンスがつくられる
コンストラクタで渡した値で初期化される

@TestAnnotation1(version = "1.05", author = "taro")    //アノテーションクラスのコンストラクタみたいのもの。
class Sample1                                          //内部にアノテーションクラスのインスタンスをもつ

---- ▽ アノテーションクラス　を使った処理を定義 -------
2.アノテーションクラスを内部に持つクラスの"クラス.class"を取得
Class<?> c1 = Class.forName("Sample1");

3.アノテーションクラスのインスタンスを取得
"クラス.class"から内部のアノテーションクラスのインスタンスを取得
("クラス.class"でないと取得できない)
TestAnnotation1 a1 =c1.getAnnotation(TestAnnotation1.class);

4.アノテーションのインスタンスから、アノテーションクラスのコンストラクタで渡した値を取得できる
a1.version().equals("1.05")

[リフレクション]
クラス内部のプロパティ、メソッドの情報を全て取得できるので、
内部に作成されたアノテーションクラスにもアクセスできる
------------------------------------------------------*/
/**
 * @アノテーションクラス
 *
 * https://itsakura.com/java-annotation-make
 * http://www.ne.jp/asahi/hishidama/home/tech/java/annotation.html
 * https://qiita.com/k5n/items/3eaafc5aa5a07cd02f5c　（Kotlin）
 *
 */

/*----------------------------------------------------
               @アノテーションクラス定義
----------------------------------------------------
@Retention(アノテーションを保持する範囲)
@Target(アノテーションを適用する要素)
public @interface アノテーション名{}
 ----------------------------------------------------*/


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Special

//コンストラクタなし
@Target(AnnotationTarget.CLASS)
annotation class Special1


//コンストラクタあり
@Target(AnnotationTarget.CLASS)
annotation class Special2(val why: String)




/*----------------------------------------------------
               @アノテーションを適用
 ----------------------------------------------------*/
//コンストラクタなし
@Special1
class Foo {}

//コンストラクタあり
@Special2("example")        //パラメータは順番が定義されているのでパラメータ名は不要
class Foo2 {}

/*----------------------------------------------------
              サンプル
 ----------------------------------------------------*/
//@アノテーションクラス定義
@Retention(AnnotationRetention.RUNTIME)
annotation class StringAnn(val value: String)


//@アノテーションを適用
@StringAnn("クラスです")
class Sample @StringAnn("コンストラクダです")
constructor() {

    @StringAnn("フィールドです")
    var n: Int = 0

    @StringAnn("メッソドです")
    fun function(
            @StringAnn("ひきすう1") param: Int,
            @StringAnn("ひきすう2") p2: Int
    ) {

        @StringAnn("ローカル変数です")
        val a = 1
    }
}

//
fun main(args: Array<String>) {
    val clazz = Sample::class.java

    dump("クラス", clazz.declaredAnnotations)

    val cs = clazz.constructors
    dump("コンストラクター", cs[0].declaredAnnotations)

    val fs = clazz.declaredFields
    dump("フィールド", fs[0].declaredAnnotations)

    val ms = clazz.declaredMethods
    dump("メソッド", ms[0].declaredAnnotations)

    val ma = ms[0].parameterAnnotations
    dump("引数1", ma[0])
    dump("引数2", ma[1])

    //ローカル変数のアノテーションはどうやって取得するんだろう？？
}

fun dump(message: String, `as`: Array<Annotation>) {
    println(message)
    for (a in `as`) {
        dump1(a)
    }
}

fun dump1(a: Annotation) {
    if (a is StringAnn) {
        val s = a as StringAnn
        System.out.println(s.value)
    }
}


/*----------------------------------------------------
              サンプル
 ----------------------------------------------------*/
//@アノテーションクラス定義
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class TestAnn(val myVersion: String = "1", val author: String){

}

//@アノテーションを適用

class Sample1 {
    @TestAnn("2.01", "jiro")
    fun print() {
        println("test")
    }
}

object Test1 {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {

        //クラス名から、クラス.class作成
        val c1 = Class.forName("test1.Sample1")
        val c2 = Sample1::class

        //クラス.classの メソッド取得
        val method = c1.getMethod("print")
        //クラス.classの アノテーションクラス取得
        val b1 = method.getAnnotation(TestAnn::class.java) as TestAnn

        if (b1.myVersion.equals("2.01")) {
            println("2.01です")
        }
        if (b1.author.equals("jiro")) {
            println("jiroです")
        }
    }
}
