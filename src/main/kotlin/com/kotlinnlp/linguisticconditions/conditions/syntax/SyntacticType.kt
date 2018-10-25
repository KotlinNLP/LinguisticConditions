/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.conditions.syntax

import com.beust.klaxon.JsonObject
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken
import com.kotlinnlp.linguisticdescription.syntax.SyntacticType

/**
 * The condition that verifies a component of a syntactic type of a token.
 *
 * @property value the syntactic type to be verified
 */
internal class SyntacticType(val value: SyntacticType) : Condition() {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "syn"
  }

  /**
   * Build a [SyntacticType] condition from a JSON object.
   *
   * @param jsonObject the JSON object that represents a [SyntacticType] condition
   *
   * @return a new condition interpreted from the given [jsonObject]
   */
  constructor(jsonObject: JsonObject): this(SyntacticType.byAnnotation(jsonObject.string("value")!!))

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
    token != null && token.syntacticRelation.dependency.type == this.value
}
