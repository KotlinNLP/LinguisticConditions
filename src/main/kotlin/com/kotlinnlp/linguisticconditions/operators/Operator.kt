/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.operators

import com.beust.klaxon.JsonObject
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticconditions.InvalidOperatorType
import com.kotlinnlp.linguisticconditions.MissingValue
import com.kotlinnlp.linguisticconditions.TooManyValues
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * The condition that represents an operator to apply to other conditions.
 */
internal sealed class Operator : Condition() {

  /**
   * An [Operator] factory.
   */
  companion object Factory {

    /**
     * The map of operator types to classes.
     */
    private val classesMap = mapOf(
      And.ANNOTATION to And::class,
      Or.ANNOTATION to Or::class,
      Xor.ANNOTATION to Xor::class,
      Not.ANNOTATION to Not::class,
      AllDescendants.ANNOTATION to AllDescendants::class,
      AnyDescendant.ANNOTATION to AnyDescendant::class,
      AnyDirectDescendant.ANNOTATION to AnyDirectDescendant::class,
      AllDirectDescendants.ANNOTATION to AllDirectDescendants::class
    )

    /**
     * Build an [Operator] from a JSON object.
     *
     * @param jsonObject the JSON object that represents an operator
     *
     * @return a new operator interpreted from the given [jsonObject]
     */
    operator fun invoke(jsonObject: JsonObject): Operator {

      if (jsonObject.keys.size == 0) throw MissingValue()
      if (jsonObject.keys.size > 1) throw TooManyValues()

      val operatorType: String = jsonObject.keys.first()

      if (operatorType !in classesMap) throw InvalidOperatorType(operatorType)

      val operatorClass: KClass<out Operator> = classesMap.getValue(operatorType)

      return operatorClass.constructors.first().call(
        if (operatorClass.isSubclassOf(Single::class))
          Condition(jsonObject.obj(operatorType)!!)
        else
          jsonObject.array<JsonObject>(operatorType)!!.map { Condition(it) }
      )
    }
  }

  /**
   * @property condition the condition to which this operator is applied
   */
  abstract class Single(val condition: Condition) : Operator()

  /**
   * @property conditions the conditions to which this operator is applied
   */
  abstract class Multiple(val conditions: List<Condition>) : Operator()
}
