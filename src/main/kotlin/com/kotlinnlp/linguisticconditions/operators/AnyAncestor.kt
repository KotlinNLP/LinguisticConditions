/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.operators

import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The 'any-ancestor' operator.
 * It verifies that at least one ancestor of a given token verifies the [condition].
 *
 * @param condition the condition to which this operator is applied
 */
internal class AnyAncestor(condition: Condition) : Operator.Single(condition) {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "any-ancestor"
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

    if (token == null) return false

    var verified = false

    dependencyTree.forEachAncestor(token.id) {

      val ancestor: MorphoSynToken.Single = tokens[dependencyTree.getPosition(it)]

      if (this.condition.isVerified(token = ancestor, tokens = tokens, dependencyTree = dependencyTree)) {
        verified = true
        return@forEachAncestor
      }
    }

    return verified
  }
}
