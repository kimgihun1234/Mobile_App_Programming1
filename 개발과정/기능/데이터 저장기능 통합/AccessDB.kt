package com.example.mobile_app

interface AccessDB {
    public fun insert(date:String, count:Int)
    public fun inputHash(hash: HashMap<String, Int>)
}