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
import com.kotlinnlp.linguisticconditions.conditions.DoubleCondition
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
    private val classesMap: Map<String, KClass<out Operator>> = mapOf(
      And.ANNOTATION to And::class,
      Or.ANNOTATION to Or::class,
      Xor.ANNOTATION to Xor::class,
      Not.ANNOTATION to Not::class,
      AllDescendants.ANNOTATION to AllDescendants::class,
      AnyDescendant.ANNOTATION to AnyDescendant::class,
      AnyDirectDescendant.ANNOTATION to AnyDirectDescendant::class,
      AllDirectDescendants.ANNOTATION to AllDirectDescendants::class,
      MatchDirectDescendants.ANNOTATION to MatchDirectDescendants::class,
      AllAncestors.ANNOTATION to AllAncestors::class,
      AnyAncestor.ANNOTATION to AnyAncestor::class
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
      val operatorClass: KClass<out Operator> = classesMap[operatorType] ?: throw InvalidOperatorType(operatorType)

      val args: Array<*> = when {

        operatorClass.isSubclassOf(Single::class) -> arrayOf(
          Condition(jsonObject.obj(operatorType)!!)
        )

        operatorClass.isSubclassOf(Multiple::class) -> arrayOf(
          jsonObject.array<JsonObject>(operatorType)!!.map { Condition(it) }
        )

        else -> jsonObject.obj(operatorType)!!.let {
          arrayOf(
            Condition(it.obj("target")!!),
            Condition(it.obj("reference")!!),
            DoubleCondition(it.obj("condition")!!)
          )
        }
      }

      return operatorClass.constructors.first().call(*args)
    }
  }

  /**
   * An operator that verifies a single condition on a token.
   *
   * @param condition the condition to which this operator is applied
   */
  abstract class Single(protected val condition: Condition) : Operator() {

    /**
     * Whether this operator looks at a single token, without requiring to check other tokens properties.
     */
    override val isUnary: Boolean = this.condition.isUnary

    /**
     * Whether this operator looks at a dependent-governor tokens pair, without requiring to check other tokens
     * properties.
     */
    override val isBinary: Boolean = this.condition.isBinary

    /**
     * Whether this operator needs to look at the morphology.
     */
    override val checkMorpho: Boolean = this.condition.checkMorpho

    /**
     * Whether this operator needs to look at the morphological properties.
     */
    override val checkMorphoProp: Boolean = this.condition.checkMorphoProp

    /**
     * Whether this operator needs to look at the context morphology.
     */
    override val checkContext: Boolean = this.condition.checkContext
  }

  /**
   * An operator that verifies more conditions on a token.
   *
   * @param conditions the conditions to which this operator is applied
   */
  abstract class Multiple(protected val conditions: List<Condition>) : Operator() {

    /**
     * Whether this operator looks at a single token, without requiring to check other tokens properties.
     */
    override val isUnary: Boolean = this.conditions.all { it.isUnary }

    /**
     * Whether this operator looks at a dependent-governor tokens pair, without requiring to check other tokens
     * properties.
     */
    override val isBinary: Boolean =
      this.conditions.all { it.isUnary || it.isBinary } && this.conditions.any { it.isBinary }

    /**
     * Whether this operator needs to look at the morphology.
     */
    override val checkMorpho: Boolean = this.conditions.any { it.checkMorpho }

    /**
     * Whether this operator needs to look at the morphological properties.
     */
    override val checkMorphoProp: Boolean = this.conditions.any { it.checkMorphoProp }

    /**
     * Whether this operator needs to look at the context morphology.
     */
    override val checkContext: Boolean = this.conditions.any { it.checkContext }
  }

  /**
   * An operator that verifies a double condition on pairs of target-reference tokens, extracted with a target and a
   * reference conditions.
   *
   * @param target the condition to match the target tokens
   * @param reference the condition to match the reference tokens
   * @param condition the double condition to verify on all the pairs of target-reference tokens
   */
  abstract class Match(
    protected val target: Condition,
    protected val reference: Condition,
    protected val condition: DoubleCondition
  ) : Operator() {

    /**
     * Whether this operator looks at a single token, without requiring to check other tokens properties.
     */
    override val isUnary: Boolean = sequenceOf(this.target, this.reference, this.condition).all { it.isUnary }

    /**
     * Whether this operator looks at a dependent-governor tokens pair, without requiring to check other tokens
     * properties.
     */
    override val isBinary: Boolean = sequenceOf(this.target, this.reference, this.condition).let { c ->
      c.all { it.isUnary || it.isBinary } && c.any { it.isBinary }
    }

    /**
     * Whether this operator needs to look at the morphology.
     */
    override val checkMorpho: Boolean = sequenceOf(this.target, this.reference, this.condition).any { it.checkMorpho }

    /**
     * Whether this operator needs to look at the morphological properties.
     */
    override val checkMorphoProp: Boolean =
      sequenceOf(this.target, this.reference, this.condition).any { it.checkMorphoProp }

    /**
     * Whether this operator needs to look at the context morphology.
     */
    override val checkContext: Boolean = sequenceOf(this.target, this.reference, this.condition).any { it.checkContext }
  }
}
