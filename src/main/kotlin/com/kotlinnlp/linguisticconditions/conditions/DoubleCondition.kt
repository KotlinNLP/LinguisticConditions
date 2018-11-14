/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.LinguisticCondition
import com.kotlinnlp.linguisticconditions.MissingJSONConstructor
import com.kotlinnlp.linguisticconditions.MissingValue
import com.kotlinnlp.linguisticconditions.TooManyValues
import com.kotlinnlp.linguisticconditions.conditions.agreement.TokensAgreement
import com.kotlinnlp.linguisticconditions.conditions.distance.TokensDistance
import com.kotlinnlp.linguisticconditions.conditions.position.TokensRelativePosition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * A condition that can be verified on two morpho-syntactic tokens.
 */
internal abstract class DoubleCondition : LinguisticCondition {

  /**
   * A [DoubleCondition] factory.
   */
  companion object Factory {

    /**
     * The map of condition types to classes.
     */
    private val classesMap: Map<String, KClass<out DoubleCondition>> = mapOf(
      TokensAgreement.ANNOTATION to TokensAgreement::class,
      TokensDistance.ANNOTATION to TokensDistance::class,
      TokensRelativePosition.ANNOTATION to TokensRelativePosition::class
    )

    /**
     * Build a [DoubleCondition] from a JSON object.
     *
     * @param jsonObject the JSON object that represents a double condition
     *
     * @return a new double condition interpreted from the given [jsonObject]
     */
    operator fun invoke(jsonObject: JsonObject): DoubleCondition {

      if (jsonObject.keys.size == 0) throw MissingValue()
      if (jsonObject.keys.size > 1) throw TooManyValues()

      val conditionType: String = jsonObject.keys.single()
      val constructors: List<KFunction<DoubleCondition>> = classesMap.getValue(conditionType).constructors.toList()

      for (i in 0 until constructors.size) {
        try {
          return constructors[i].call(jsonObject.obj(conditionType)!!)
        } catch (e: IllegalArgumentException) {}
      }

      throw MissingJSONConstructor(conditionType)
    }
  }

  /**
   * @param tokenA a token of the sentence
   * @param tokenB a token of the sentence
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the two given tokens
   */
  abstract fun isVerified(tokenA: MorphoSynToken.Single,
                          tokenB: MorphoSynToken.Single,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean
}
