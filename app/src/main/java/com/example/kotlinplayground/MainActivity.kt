package com.example.kotlinplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //flowBasics()
        //flowOperators()
        //flowContext()
        flowShared()
    }

    private fun flowBasics() {
        GlobalScope.launch {
            val flow = producer()
            flow.collect{
                Log.d("###", "onCreate: $it")
            }
        }
    }

    private fun flowOperators() {
        GlobalScope.launch{
            val data =producer2()
            val time = measureTimeMillis {
                data
                    .buffer(3)
                    .map {
                        it+1
                    }.filter {
                        it % 2 == 0
                    }.collect {
                        delay(1000)
                        Log.d("###", "flowOperators: $it")
                    }
            }
            Log.d("###", "flowOperators: $time")
        }
    }

    private fun flowEvents() {
        GlobalScope.launch {
            val data = producer()
            data
                .onStart {
                    emit(0)
                    Log.d("###", "flowOperatorsStart: ")
                }
                .onCompletion {
                    emit(8)
                    Log.d("####", "flowOperatorsComplete: ")
                }
                .onEach {
                    Log.d("###", "flowOperators:$it ")
                }
                .collect{

                }
        }
    }

    private fun flowContext() {
        GlobalScope.launch(Dispatchers.Main) {
            val data = producerContext()
            data
                .flowOn(Dispatchers.IO)
                .collect {
                    Log.d("###", "flowContext:$it ")
                }
        }
    }

    private fun flowShared() {
        GlobalScope.launch {
            producerSharedFlow()
                .collect {
                    Log.d("###", "flowShared1: $it")
                }
        }

        GlobalScope.launch {
            producerSharedFlow()
                .collect {
                    delay(2000)
                    Log.d("###", "flowShared2: $it")
                }
        }
    }

    private fun producer() = flow<Int> {
        val list = listOf<Int>(1,2,3,4,5,6,7)
        delay(3000)
        list.forEach {
            emit(it)
        }
    }

    private fun producer2() : Flow<Int> {
        val list = listOf<Int>(1,2,3,4,5,6,7,8)
        return list.asFlow()
    }

    private fun producerContext() : Flow<Int> {
        return flow<Int> {
            val list = listOf<Int>(1,2,3,4,5,6,7,8)
            list.forEach {
                emit(it)
            }
        }
    }

    private fun producerSharedFlow() : Flow<Int> {
        val mutableSharedFlow = MutableSharedFlow<Int>()
        GlobalScope.launch {
            val list = listOf<Int>(1,2,3,4,5,6,7,8)
            list.forEach {
                mutableSharedFlow.emit(it)
                delay(1000)
            }
        }
        return mutableSharedFlow
    }
}