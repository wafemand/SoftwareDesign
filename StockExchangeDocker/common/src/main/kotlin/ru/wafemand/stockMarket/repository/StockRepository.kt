package ru.wafemand.stockMarket.repository

import ru.wafemand.stockMarket.model.Company
import ru.wafemand.stockMarket.model.User

interface StockRepository {
    fun getAllCompanies(): List<Company>
    
    fun getCompany(name: String): Company?
    
    fun addCompany(company: Company)
    
    fun updateCompany(name: String, company: Company)

    fun getAllUsers(): List<User>
    
    fun getUser(login: String): User?
    
    fun addUser(user: User)
    
    fun updateUser(login: String, user: User)
    
    fun clearAll()
}