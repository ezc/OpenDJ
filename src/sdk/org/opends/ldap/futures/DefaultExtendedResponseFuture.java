package org.opends.ldap.futures;



import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.opends.ldap.ErrorResultException;
import org.opends.ldap.ExtendedRequest;
import org.opends.ldap.ExtendedResponse;
import org.opends.ldap.ExtendedResponseHandler;
import org.opends.ldap.IntermediateResponse;
import org.opends.ldap.impl.LDAPConnection;



/**
 * Created by IntelliJ IDEA. User: boli Date: Jul 8, 2009 Time: 2:12:46
 * PM To change this template use File | Settings | File Templates.
 */
public final class DefaultExtendedResponseFuture extends
    AbstractResponseFuture<ExtendedRequest, ExtendedResponse> implements
    ExtendedResponseFuture
{
  private class IntermediateResultInvoker implements Runnable
  {
    IntermediateResponse intermediateResult;



    public void run()
    {
      ((ExtendedResponseHandler) handler)
          .handleIntermediateResponse(intermediateResult);
      invokerLock.release();
    }
  }



  private final Semaphore invokerLock;
  private final IntermediateResultInvoker intermediateInvoker =
      new IntermediateResultInvoker();

  private int numIntermediateResponses;



  public DefaultExtendedResponseFuture(int messageID,
      ExtendedRequest orginalRequest,
      ExtendedResponseHandler extendedResponseHandler,
      LDAPConnection connection, ExecutorService handlerExecutor)
  {
    super(messageID, orginalRequest, extendedResponseHandler,
        connection, handlerExecutor);
    this.invokerLock = new Semaphore(1);
  }



  @Override
  public synchronized void failure(Throwable failure)
  {
    if (latch.getCount() > 0)
    {
      if (failure instanceof ExecutionException)
      {
        this.failure = (ExecutionException) failure;
      }
      else
      {
        this.failure = new ExecutionException(failure);
      }
      if (handler != null)
      {
        try
        {
          invokerLock.acquire();
          invokeHandler(this);
        }
        catch (InterruptedException ie)
        {
          // TODO: What should we do now?
        }
      }
      latch.countDown();
    }
  }



  public synchronized int getNumIntermediateResponse()
  {
    return numIntermediateResponses;
  }



  @Override
  public void run()
  {
    super.run();
    invokerLock.release();
  }



  @Override
  public synchronized void setResult(ExtendedResponse result)
  {
    if (latch.getCount() > 0)
    {
      if (result.getResultCode().isExceptional())
      {
        this.failure = new ErrorResultException(result);
      }
      else
      {
        this.result = result;
      }
      if (handler != null)
      {
        try
        {
          invokerLock.acquire();
          invokeHandler(this);
        }
        catch (InterruptedException ie)
        {
          // TODO: What should we do now?
        }
      }
      latch.countDown();
    }
  }



  public synchronized void setResult(
      IntermediateResponse intermediateResponse)
  {
    numIntermediateResponses++;
    if ((latch.getCount() > 0) && (handler != null))
    {
      try
      {
        invokerLock.acquire();
        intermediateInvoker.intermediateResult = intermediateResponse;
        invokeHandler(intermediateInvoker);
      }
      catch (InterruptedException ie)
      {
        // TODO: What should we do now?
      }
    }
  }
}
