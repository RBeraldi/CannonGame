package com.beraldi.cannongame

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import java.lang.Math.pow
import kotlin.math.*

class myView(context: Context?) : View(context), View.OnTouchListener {


    val cannon  = BitmapFactory.
    decodeStream(getContext().
    assets.open("cannon.jpg"))

    val scale = Matrix()

    val textPaint = Paint().also {
        it.color = Color.parseColor("#AAFF0000")
        it.strokeWidth = 30f
        it.textSize=40f
    }


    val cannonPaint = Paint().also {
        it.color = Color.parseColor("#AAFF0000")
        it.strokeWidth = 40f
        it.strokeCap=Paint.Cap.ROUND

    }

    val ballPaint = Paint().apply {
        color = Color.parseColor("#AAFF0000")
        strokeWidth = 1f
    }

    val testPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 4f
    }

    val cannonLenght = 200f
    val ballradius = 20f

    var firing = false

    var ballx=0f
    var bally=0f

    var vx =0f
    var vy= 0f

    val mpp = 1f //meters per pixel

    var speed = 400.0f //px/s
    var gravity = 198.2f //px^2/s
    
    var currentFireTime=0L

    var m = Matrix()

    var ar = 1f //Aspect ratio

    var a = 0.0
    init {
        setOnTouchListener(this)
        gravity*=mpp
        speed*=mpp

    }


    override fun onDraw(cv: Canvas) {
        super.onDraw(cv)


        ar = width.toFloat()/height.toFloat()


        cv.drawText("vx: "+vx.toInt()+"  vy: "+vy.toInt()+" a: "+(a*100).toInt(),30f,30f,textPaint)

        if (!firing){ //TODO: Place a random obstacle...

            cv.drawLine(0f,height.toFloat(),vx*1,
                    height.toFloat()+vy*1,testPaint)
        }

        scale.setScale(0.1f,0.1f)
        scale.postTranslate(0f-50,height.toFloat()-cannon.height*0.1f)
        scale.postConcat(m)
        cv.drawBitmap(cannon,scale,null)
        cv.withMatrix(m) {
            drawLine(
                -100f, height.toFloat(),
                cannonLenght,
                height.toFloat(),
                cannonPaint
            )



        }

        if (firing) {
            val now=System.currentTimeMillis()
            val dt = now-currentFireTime
            currentFireTime=now

            ballx+=vx*dt/1000f

            bally+=vy*dt/1000f

            val dv=gravity*dt/1000f

            vy+=dv


            cv.drawCircle(ballx,bally,ballradius,ballPaint)

            invalidate()
            if ((ballx>width) or (bally>height) or (ballx<0) ){
                firing=false
            }
            Log.i("info", "onDraw: "+ballx+" "+bally)
        }



    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                if (!firing) {
                    //val ar = 1f
                    a =0.0// atan2( (height - event.y), 1/ar*(width - event.x))

                    val sen=height.toDouble()-event.y
                    val mod=sqrt(pow(sen,2.0)+pow(event.x.toDouble(),2.0))

                    a=asin(sen/mod)
                    m.setRotate(-a.toFloat()*180/Math.PI.toFloat(), 0f, height.toFloat())
                    //Log.i("info", "onTouch: "+event.x+" "+event.y+" "+a.toString())

                    val aa=3.0f*mod.toFloat()/sqrt((height*height).toFloat()+(width*width).toFloat())

                    vx = aa*speed*cos(a.toFloat())
                    vy = -aa*speed*sin(a.toFloat())
                    Log.i("info3", "onTouch[position angle]: "+aa)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (firing) return true
                firing = true
                ballx=0f;bally=height.toFloat()
                currentFireTime=System.currentTimeMillis()
                invalidate()
            }

        }
        return true
    }

}
