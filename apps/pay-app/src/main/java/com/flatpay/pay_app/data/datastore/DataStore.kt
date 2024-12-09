package com.flatpay.pay_app.data.datastore

class DataStore {
    lateinit var stores : List<DataStoreObject>
    // Example of a simple data retrieval implementation
    fun getData(): List<DataStoreObject> {
        return stores
    }
}