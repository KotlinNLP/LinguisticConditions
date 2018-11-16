/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.distance

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the distance of a token from its governor.
 *
 * @param value the value of the distance to verify
 */
internal class DistanceFromGovernor(override val value: Int) : Distance, Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "distance-from-governor"
  }

  /**
   * Build a [DistanceFromGovernor] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [DistanceFromGovernor] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(jsonObject.int("value")!!)

  /**
   * Whether this condition looks at a single token, without requiring to check other tokens properties.
   */
  override val isUnary: Boolean = false

  /**
   * Whether this condition needs to look at the morphology.
   */
  override val checkMorpho: Boolean = false

  /**
   * Whether this condition needs to look at the morphological properties.
   */
  override val checkMorphoProp: Boolean = false

  /**
   * Whether this condition needs to look at the context morphology.
   */
  override val checkContext: Boolean = false

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken.Single?,
                          tokens: List<MorphoSynToken.Single>,
                          dependencyTree: DependencyTree): Boolean =
    token != null &&
      this.isVerified(targetId = token.id, refId = dependencyTree.getHead(token.id), dependencyTree = dependencyTree)
}
