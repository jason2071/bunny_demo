package com.example.bunny.mockup_data


sealed class UserResult
data class Success(val users: List<String>) : UserResult()
data class Failure(val message: String) : UserResult()


sealed class TypeModel {
    abstract var type: String
}

data class Mode(override var type: String, var mode: String) : TypeModel()
data class Time(override var type: String, var time: String) : TypeModel()