package org.opends.common.api.raw.request.filter;

import org.opends.server.types.ByteString;
import org.opends.server.util.Validator;
import org.opends.common.api.AttributeDescription;

/**
 * Created by IntelliJ IDEA. User: digitalperk Date: Jun 10, 2009 Time: 11:54:17
 * AM To change this template use File | Settings | File Templates.
 */
public abstract class RawAssertionFilter extends RawFilter
{
  protected String attributeDescription;
  protected ByteString assertionValue;

  protected RawAssertionFilter(String attributeDescription,
                               ByteString assertionValue)
  {
    Validator.ensureNotNull(attributeDescription, assertionValue);
    this.attributeDescription = attributeDescription;
    this.assertionValue = assertionValue;
  }

  public String getAttributeType()
  {
    return attributeDescription;
  }

  public RawAssertionFilter setAttributeDescription(String attributeDescription)
  {
    Validator.ensureNotNull(attributeDescription);
    this.attributeDescription = attributeDescription;
    return this;
  }

  public RawAssertionFilter setAttributeDescription(AttributeDescription attributeDescription)
  {
    Validator.ensureNotNull(attributeDescription);
    this.attributeDescription = attributeDescription.toString();
    return this;
  }

  public ByteString getAssertionValue()
  {
    return assertionValue;
  }

  public RawAssertionFilter setAssertionValue(ByteString assertionValue)
  {
    Validator.ensureNotNull(assertionValue);
    this.assertionValue = assertionValue;
    return this;
  }
}
