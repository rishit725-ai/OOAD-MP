package com.disaster.response.command;

/**
 * COMMAND PATTERN — Behavioral Pattern
 *
 * All concrete commands implement this interface.
 * Encapsulates a request as an object, enabling:
 *   - Parameterisation of actions
 *   - Queuing / logging of operations (CommandInvoker)
 *   - Undo support
 */
public interface DisasterCommand {

    /** Execute the command action. */
    void execute();

    /** Undo the command action (revert state). */
    void undo();

    /** Human-readable description for the audit log. */
    String getDescription();

    /** Short type label (e.g. "ASSIGN_TEAM", "UPDATE_STATUS"). */
    String getCommandType();
}
