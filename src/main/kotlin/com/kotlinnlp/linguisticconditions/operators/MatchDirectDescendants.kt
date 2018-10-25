/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions.operators

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.linguisticconditions.Condition
import com.kotlinnlp.linguisticconditions.conditions.DoubleCondition
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * The condition that verifies a double condition on all the pairs of direct descendants of a token that match a target
 * and a reference condition.
 *
 * @param target the condition to match the target direct descendants
 * @param reference the condition to match the reference direct descendants
 * @param condition the double condition to verify on all the pairs of direct descendants that match the target and
 *                  the reference conditions
 */
internal class MatchDirectDescendants(
  target: Condition,
  reference: Condition,
  condition: DoubleCondition
) : Operator.Match(target = target, reference = reference, condition = condition) {

  companion object {

    /**
     * The annotation of the condition.
     */
    const val ANNOTATION: String = "match-direct-descendants"
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

    val descendants: List<MorphoSynToken.Single> = descendantsIds.map { tokens[dependencyTree.getPosition(it)] }

    val targets: List<MorphoSynToken.Single> = descendants.filter {
      this.target.isVerified(token = it, tokens = tokens, dependencyTree = dependencyTree)
    }
    val references: List<MorphoSynToken.Single> = descendants.filter {
      this.reference.isVerified(token = it, tokens = tokens, dependencyTree = dependencyTree)
    }

    return targets.all { target ->
      references.all { reference ->
        this.condition.isVerified(tokenA = target, tokenB = reference, tokens = tokens, dependencyTree = dependencyTree)
      }
    }
  }
}
