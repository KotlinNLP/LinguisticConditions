/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.linguisticconditions.operators.Operator
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken
import kotlin.reflect.KFunction

/**
 * A condition that can be verified on a morpho-syntactic token.
 */
abstract class Condition {

  /**
   * A [Condition] factory.
   */
  companion object Factory {

    /**
     * The map of condition types to classes.
     */
    private val classesMap = mapOf(
      CountDescendants.ANNOTATION to CountDescendants::class,
      CountDirectDescendants.ANNOTATION to CountDirectDescendants::class,
      SyntacticType.ANNOTATION to SyntacticType::class,
      SyntacticTypePartial.ANNOTATION to SyntacticTypePartial::class,
      Distance.ANNOTATION to Distance::class,
      Morphology.ANNOTATION to Morphology::class,
      AgreementWithGovernor.ANNOTATION to AgreementWithGovernor::class,
      Pos.ANNOTATION to Pos::class,
      PosPartial.ANNOTATION to PosPartial::class,
      Position.ANNOTATION to Position::class,
      GovernorRelativePosition.ANNOTATION to GovernorRelativePosition::class
    )

    /**
     * Build a [Condition] from a JSON object.
     *
     * @param jsonObject the JSON object that represents a condition
     *
     * @return a new condition interpreted from the given [jsonObject]
     */
    operator fun invoke(jsonObject: JsonObject): Condition {

      if (jsonObject.keys.size == 0) throw MissingValue()
      if (jsonObject.keys.size > 1) throw TooManyValues()

      val conditionType: String = jsonObject.keys.single()

      return if (conditionType in classesMap)
        constructWithJsonObject(conditionType = conditionType, jsonObject = jsonObject.obj(conditionType)!!)
      else
        Operator(jsonObject)
    }

    /**
     * Build a [Condition] of a given type using its representing JSON object.
     *
     * @param conditionType the type of the [Condition]
     * @param jsonObject the JSON object that represents the [Condition]
     *
     * @throws MissingJSONConstructor when a valid constructor is not found for the given condition
     *
     * @return a new instance of the given condition
     */
    private fun constructWithJsonObject(conditionType: String, jsonObject: JsonObject): Condition {

      val constructors: List<KFunction<Condition>> = classesMap.getValue(conditionType).constructors.toList()

      for (i in 0 until constructors.size) {
        try {
          return constructors[i].call(jsonObject)
        } catch (e: IllegalArgumentException) {}
      }

      throw MissingJSONConstructor(conditionType)
    }
  }

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  abstract fun isVerified(token: MorphoSynToken.Single?,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean
}
