/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opendj3/legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opendj3/legal-notices/CDDLv1_0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2010 Sun Microsystems, Inc.
 *      Portions copyright 2012 ForgeRock AS.
 */

package org.forgerock.opendj.ldap.responses;



import java.util.LinkedList;
import java.util.List;

import org.forgerock.opendj.ldap.DecodeException;
import org.forgerock.opendj.ldap.DecodeOptions;
import org.forgerock.opendj.ldap.controls.Control;
import org.forgerock.opendj.ldap.controls.ControlDecoder;
import org.forgerock.opendj.ldap.controls.GenericControl;

import com.forgerock.opendj.util.Validator;


/**
 * Modifiable response implementation.
 *
 * @param <S>
 *          The type of response.
 */
abstract class AbstractResponseImpl<S extends Response> implements Response
{
  private final List<Control> controls = new LinkedList<Control>();



  /**
   * Creates a new modifiable response implementation.
   */
  AbstractResponseImpl()
  {
    // No implementation required.
  }



  /**
   * Creates a new abstract response that is an exact copy of the provided
   * response.
   *
   * @param response
   *          The response to be copied.
   * @throws NullPointerException
   *           If {@code response} was {@code null} .
   */
  AbstractResponseImpl(Response response)
  {
    Validator.ensureNotNull(response);
    for (Control control : response.getControls())
    {
      // Create defensive copy.
      controls.add(GenericControl.newControl(control));
    }
  }



  /**
   * {@inheritDoc}
   */
  public final S addControl(final Control control)
  {
    Validator.ensureNotNull(control);
    controls.add(control);
    return getThis();
  }



  /**
   * {@inheritDoc}
   */
  public final <C extends Control> C getControl(
      final ControlDecoder<C> decoder, final DecodeOptions options)
      throws DecodeException
  {
    Validator.ensureNotNull(decoder, options);

    // Avoid creating an iterator if possible.
    if (controls.isEmpty())
    {
      return null;
    }

    for (final Control control : controls)
    {
      if (control.getOID().equals(decoder.getOID()))
      {
        return decoder.decodeControl(control, options);
      }
    }

    return null;
  }



  /**
   * {@inheritDoc}
   */
  public final List<Control> getControls()
  {
    return controls;
  }



  @Override
  public abstract String toString();



  abstract S getThis();

}
