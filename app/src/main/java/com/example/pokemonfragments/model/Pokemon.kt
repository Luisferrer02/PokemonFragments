package com.example.pokemonfragments.model

import java.io.Serializable

class Pokemon(
    val id: Int?=null,
    val name: String?=null,
    val height: Int?=null,
    val weight: Int?=null
    ,val spriteUrl: String?=null
): Serializable