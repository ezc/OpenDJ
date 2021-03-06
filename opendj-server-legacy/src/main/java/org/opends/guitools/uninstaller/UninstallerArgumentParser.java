/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2008-2010 Sun Microsystems, Inc.
 *      Portions Copyright 2014-2015 ForgeRock AS.
 */
package org.opends.guitools.uninstaller;

import static com.forgerock.opendj.cli.ArgumentConstants.*;
import static com.forgerock.opendj.cli.CliMessages.*;
import static com.forgerock.opendj.cli.Utils.*;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.i18n.LocalizableMessageBuilder;
import org.opends.quicksetup.UserData;
import org.opends.server.admin.client.cli.SecureConnectionCliArgs;
import org.opends.server.admin.client.cli.SecureConnectionCliParser;
import org.opends.server.core.DirectoryServer.DirectoryServerVersionHandler;

import com.forgerock.opendj.cli.Argument;
import com.forgerock.opendj.cli.ArgumentException;
import com.forgerock.opendj.cli.BooleanArgument;
import com.forgerock.opendj.cli.CommonArguments;
import com.forgerock.opendj.cli.ReturnCode;
import com.forgerock.opendj.cli.StringArgument;

/** Class used to parse and populate the arguments of the Uninstaller. */
public class UninstallerArgumentParser extends SecureConnectionCliParser
{
  private BooleanArgument cliArg;
  private BooleanArgument noPromptArg;
  BooleanArgument forceOnErrorArg;
  private BooleanArgument quietArg;
  private BooleanArgument removeAllArg;
  private BooleanArgument removeServerLibrariesArg;
  private BooleanArgument removeDatabasesArg;
  private BooleanArgument removeLogFilesArg;
  private BooleanArgument removeConfigurationFilesArg;
  private BooleanArgument removeBackupFilesArg;
  private BooleanArgument removeLDIFFilesArg;

  private StringArgument referencedHostNameArg;

  /** This CLI is always using the administration connector with SSL. */
  private final boolean alwaysSSL = true;

  /**
   * Creates a new instance of this argument parser with no arguments.
   *
   * @param mainClassName
   *          The fully-qualified name of the Java class that should
   *          be invoked to launch the program with which this
   *          argument parser is associated.
   * @param toolDescription
   *          A human-readable description for the tool, which will be
   *          included when displaying usage information.
   * @param longArgumentsCaseSensitive
   *          Indicates whether subcommand and long argument names
   *          should be treated in a case-sensitive manner.
   */
  public UninstallerArgumentParser(String mainClassName,
      LocalizableMessage toolDescription, boolean longArgumentsCaseSensitive)
  {
    super(mainClassName, toolDescription, longArgumentsCaseSensitive);
    setShortToolDescription(REF_SHORT_DESC_UNINSTALL.get());
    setVersionHandler(new DirectoryServerVersionHandler());
  }

  /**
   * Initialize Global option.
   *
   * @param outStream
   *          The output stream used for the usage.
   * @throws ArgumentException
   *           If there is a problem with any of the parameters used
   *           to create this argument.
   */
  public void initializeGlobalArguments(OutputStream outStream)
  throws ArgumentException
  {
    LinkedHashSet<Argument> args = new LinkedHashSet<>();
    cliArg = CommonArguments.getCLI();
    args.add(cliArg);

    removeAllArg = new BooleanArgument(
        "remove-all",
        'a',
        "remove-all",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_ALL.get()
        );
    args.add(removeAllArg);
    removeServerLibrariesArg = new BooleanArgument(
        "server-libraries",
        'l',
        "server-libraries",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_SERVER_LIBRARIES.get()
        );
    args.add(removeServerLibrariesArg);
    removeDatabasesArg = new BooleanArgument(
        "databases",
        'd',
        "databases",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_DATABASES.get()
        );
    args.add(removeDatabasesArg);
    removeLogFilesArg = new BooleanArgument(
        "log-files",
        'L',
        "log-files",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_LOG_FILES.get()
        );
    args.add(removeLogFilesArg);
    removeConfigurationFilesArg = new BooleanArgument(
        "configuration-files",
        'c',
        "configuration-files",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_CONFIGURATION_FILES.get()
        );
    args.add(removeConfigurationFilesArg);
    removeBackupFilesArg = new BooleanArgument(
        "backup-files",
        'b',
        "backup-files",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_BACKUP_FILES.get()
        );
    args.add(removeBackupFilesArg);
    removeLDIFFilesArg = new BooleanArgument(
        "ldif-files",
        'e',
        "ldif-files",
        INFO_UNINSTALLDS_DESCRIPTION_REMOVE_LDIF_FILES.get()
        );
    args.add(removeLDIFFilesArg);

    noPromptArg = CommonArguments.getNoPrompt();
    args.add(noPromptArg);

    forceOnErrorArg = new BooleanArgument(
        "forceOnError",
        'f',
        "forceOnError",
        INFO_UNINSTALLDS_DESCRIPTION_FORCE.get(
            "--"+noPromptArg.getLongIdentifier()));
    args.add(forceOnErrorArg);

    quietArg = CommonArguments.getQuiet();
    args.add(quietArg);

    for (Argument arg : args)
    {
      arg.setPropertyName(arg.getLongIdentifier());
    }

    ArrayList<Argument> defaultArgs = new ArrayList<>(createGlobalArguments(outStream, alwaysSSL));
    int index = defaultArgs.indexOf(secureArgsList.bindDnArg);
    if (index != -1)
    {
      defaultArgs.add(index, secureArgsList.adminUidArg);
      defaultArgs.remove(secureArgsList.bindDnArg);
    }
    else
    {
      defaultArgs.add(secureArgsList.adminUidArg);
    }
    secureArgsList.adminUidArg.setHidden(false);
    defaultArgs.remove(secureArgsList.hostNameArg);
    defaultArgs.remove(secureArgsList.portArg);
    referencedHostNameArg = new StringArgument("referencedHostName",
        OPTION_SHORT_HOST,
        OPTION_LONG_REFERENCED_HOST_NAME, false, false, true,
        INFO_HOST_PLACEHOLDER.get(),
        UserData.getDefaultHostName(), OPTION_LONG_REFERENCED_HOST_NAME,
        INFO_DESCRIPTION_REFERENCED_HOST.get());
    defaultArgs.add(referencedHostNameArg);

    args.addAll(defaultArgs);
    initializeGlobalArguments(args);
  }

  /**
   * Tells whether the user specified to have an interactive uninstall or not.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to have an interactive
   * uninstall and <CODE>false</CODE> otherwise.
   */
  public boolean isInteractive()
  {
    return !noPromptArg.isPresent();
  }

  /**
   * Tells whether the user specified to force on non critical error in the non
   * interactive mode.
   * @return <CODE>true</CODE> if the user specified to force the uninstall in
   * non critical error and <CODE>false</CODE> otherwise.
   */
  public boolean isForceOnError()
  {
    return forceOnErrorArg.isPresent();
  }

  /**
   * Tells whether the user specified to have a quiet uninstall or not.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to have a quiet
   * uninstall and <CODE>false</CODE> otherwise.
   */
  public boolean isQuiet()
  {
    return quietArg.isPresent();
  }

  /**
   * Tells whether the user specified to have a verbose uninstall or not.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to have a verbose
   * uninstall and <CODE>false</CODE> otherwise.
   */
  @Override
  public boolean isVerbose()
  {
    return verboseArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove all files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove all files and
   * <CODE>false</CODE> otherwise.
   */
  public boolean removeAll()
  {
    return removeAllArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove library files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove library files and
   * <CODE>false</CODE> otherwise.
   */
  public boolean removeServerLibraries()
  {
    return removeServerLibrariesArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove database files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove database files
   * and <CODE>false</CODE> otherwise.
   */
  public boolean removeDatabases()
  {
    return removeDatabasesArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove configuration files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove configuration
   * files and <CODE>false</CODE> otherwise.
   */
  public boolean removeConfigurationFiles()
  {
    return removeConfigurationFilesArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove backup files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove backup files and
   * <CODE>false</CODE> otherwise.
   */
  public boolean removeBackupFiles()
  {
    return removeBackupFilesArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove LDIF files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove LDIF files and
   * <CODE>false</CODE> otherwise.
   */
  public boolean removeLDIFFiles()
  {
    return removeLDIFFilesArg.isPresent();
  }

  /**
   * Tells whether the user specified to remove log files.
   * This method must be called after calling parseArguments.
   * @return <CODE>true</CODE> if the user specified to remove log files and
   * <CODE>false</CODE> otherwise.
   */
  public boolean removeLogFiles()
  {
    return removeLogFilesArg.isPresent();
  }

  /**
   * Returns the default Administrator UID value.
   * @return the default Administrator UID value.
   */
  public String getDefaultAdministratorUID()
  {
    return secureArgsList.adminUidArg.getDefaultValue();
  }

  /**
   * Returns the Host name to update remote references as provided in the
   * command-line.
   * @return the Host name to update remote references as provided in the
   * command-line.
   */
  public String getReferencedHostName()
  {
    if (referencedHostNameArg.isPresent())
    {
      return referencedHostNameArg.getValue();
    }
    return null;
  }

  /**
   * Returns the default value for the Host name to update remote references as
   * provided in the command-line.
   * @return the default value for the Host name to update remote references as
   * provided in the command-line.
   */
  public String getDefaultReferencedHostName()
  {
    return referencedHostNameArg.getDefaultValue();
  }

  /**
   * Indication if provided global options are validate.
   *
   * @param buf the LocalizableMessageBuilder to write the error messages.
   * @return return code.
   */
  @Override
  public int validateGlobalOptions(LocalizableMessageBuilder buf)
  {
    if (!noPromptArg.isPresent() && forceOnErrorArg.isPresent())
    {
      final LocalizableMessage message =
          ERR_TOOL_CONFLICTING_ARGS.get(forceOnErrorArg.getLongIdentifier(),
              noPromptArg.getLongIdentifier());
      if (buf.length() > 0)
      {
        buf.append(LINE_SEPARATOR);
      }
      buf.append(message);
    }
    if (removeAllArg.isPresent())
    {
      BooleanArgument[] removeArgs = {
          removeServerLibrariesArg,
          removeDatabasesArg,
          removeLogFilesArg,
          removeConfigurationFilesArg,
          removeBackupFilesArg,
          removeLDIFFilesArg
      };
      for (BooleanArgument removeArg : removeArgs)
      {
        if (removeArg.isPresent())
        {
          LocalizableMessage message = ERR_TOOL_CONFLICTING_ARGS.get(
              removeAllArg.getLongIdentifier(),
              removeArg.getLongIdentifier());
          if (buf.length() > 0)
          {
            buf.append(LINE_SEPARATOR);
          }
          buf.append(message);
        }
      }
    }
    super.validateGlobalOptions(buf);
    if (buf.length() > 0)
    {
      return ReturnCode.CONFLICTING_ARGS.get();
    }
    return ReturnCode.SUCCESS.get();
  }

  /**
   * Returns whether the command was launched in CLI mode or not.
   * @return <CODE>true</CODE> if the command was launched to use CLI mode and
   * <CODE>false</CODE> otherwise.
   */
  public boolean isCli()
  {
    return cliArg.isPresent();
  }

  /**
   * Returns the SecureConnectionCliArgs object containing the arguments
   * of this parser.
   * @return the SecureConnectionCliArgs object containing the arguments
   * of this parser.
   */
  SecureConnectionCliArgs getSecureArgsList()
  {
    return secureArgsList;
  }
}
