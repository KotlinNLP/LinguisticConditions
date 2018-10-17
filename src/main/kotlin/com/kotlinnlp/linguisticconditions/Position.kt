/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies the position of a token.
 *
 * @param index the index of the token within the sentence real tokens
 */
class Position(private val index: Int) : Condition() {

  /**
   * The type of condition.
   */
  override val type: String = "position"

  /**
   * Build a [Position] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [Position] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject) : this(jsonObject.int("index")!!)

  /**
   * @param token a token or null if called on the virtual root
   * @param tokens the list of all the tokens that compose the sentence
   * @param dependencyTree the dependency tree of the token sentence
   *
   * @return a boolean indicating if this condition is verified for the given [token]
   */
  override fun isVerified(token: MorphoSynToken?,
                          tokens: List<MorphoSynToken>,
                          dependencyTree: DependencyTree): Boolean =
    token != null && dependencyTree.getPosition(token.id) == this.index
}
