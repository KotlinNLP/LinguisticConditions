/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.count

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that counts how many direct dependents verify a given condition.
 * Only one of the exact value, the lower or the upper bound of the count can be checked.
 *
 * @param condition the condition to verify on all the descendants of the token
 * @param value the value of the expected count
 * @param lowerThan the upper bound (inclusive) of the expected count
 * @param greaterThan the lower bound (inclusive) of the expected count
 */
internal class CountDirectDescendants(private val condition: Condition,
                                      private val value: Int?,
                                      private val lowerThan: Int?,
                                      private val greaterThan: Int?) : Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "count-direct-descendants"
  }

  /**
   * Build a [CountDirectDescendants] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [CountDirectDescendants] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(
    condition = Condition(jsonObject.obj("condition")!!),
    value = jsonObject.int("value"),
    lowerThan = jsonObject.int("lowerThan"),
    greaterThan = jsonObject.int("greaterThan"))

  /**
   * Whether this condition needs to look at the context morphology.
   */
  override val checkContext: Boolean = this.condition.checkContext

  /**
   * Check requirements.
   */
  init {
    require(listOf(this.value, this.lowerThan, this.greaterThan).count { it != null } == 1) {
      "Only one of the properties 'value', 'lowerThan' or 'greaterThan' can be defined."
    }
  }

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken.Single?,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean {

    val descendantsIds: List<Int> = if (token != null)
      dependencyTree.getDependents(token.id)
    else
      dependencyTree.getRoots() // the token is the virtual root and its direct dependents are all the roots

    val count: Int = descendantsIds.count {
      this.condition.isVerified(
        token = tokens[dependencyTree.getPosition(it)],
        tokens = tokens,
        dependencyTree = dependencyTree)
    }

    return when {
      this.value != null -> count == this.value
      this.lowerThan != null -> count <= this.lowerThan
      this.greaterThan!= null -> count >= this.greaterThan
      else -> throw RuntimeException("Invalid condition.")
    }
  }
}
