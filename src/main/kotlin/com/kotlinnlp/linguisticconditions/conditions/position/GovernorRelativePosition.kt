/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.position

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the relative position of a token with its governor.
 *
 * @param positionType the position type (right or left)
 */
internal class GovernorRelativePosition(
  override val positionType: RelativePosition.Type
) : RelativePosition, Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "position-respect-governor"
  }

  /**
   * Build a [GovernorRelativePosition] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [GovernorRelativePosition] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(RelativePosition.Type.valueOf(jsonObject.string("type")!!))

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

    if (token == null) return false

    val headId: Int? = dependencyTree.getHead(token.id)

    return when (this.positionType) {

      RelativePosition.Type.Top ->
        headId == null

      RelativePosition.Type.Left ->
        headId != null && dependencyTree.getPosition(token.id) < dependencyTree.getPosition(headId)

      RelativePosition.Type.Right ->
        headId != null && dependencyTree.getPosition(token.id) > dependencyTree.getPosition(headId)
    }
  }
}
