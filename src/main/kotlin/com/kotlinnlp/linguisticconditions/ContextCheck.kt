/* Copyright 2018-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.linguisticconditions

import com.kotlinnlp.linguisticdescription.morphology.SingleMorphology
import com.kotlinnlp.linguisticdescription.sentence.token.MorphoSynToken

/**
 * Implemented by all the conditions.
 * Make available flags that indicate whether the condition needs to look at the morphology of the tokens.
 */
interface ContextCheck {

  /**
   * Whether this condition needs to look at the morphological properties.
   */
  val checkMorpho: Boolean

  /**
   * Whether this condition needs to look at the context morphology.
   */
  val checkContext: Boolean

  /**
   * @param token a single morpho-syntactic token
   *
   * @return the morphology of the token on which to check the agreement
   */
  fun getMorphology(token: MorphoSynToken.Single): SingleMorphology =
    if (this.checkContext)
      token.contextMorphologies.single().value
    else
      token.morphologies.single().value
}
