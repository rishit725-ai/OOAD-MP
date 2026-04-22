package com.disaster.response.command;

import com.disaster.response.model.CommandHistory;
import com.disaster.response.repository.CommandHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * COMMAND PATTERN — Invoker
 *
 * Executes commands and maintains a history for undo support.
 * Also persists each action to CommandHistory for the audit trail.
 *
 * SRP: Only responsible for command execution orchestration.
 * Law of Demeter: Interacts with commands via the DisasterCommand interface only.
 */
@Component
public class CommandInvoker {

    private static final Logger log = LoggerFactory.getLogger(CommandInvoker.class);
    private static final int MAX_HISTORY = 50;

    private final Deque<DisasterCommand> commandHistory = new ArrayDeque<>();
    private final CommandHistoryRepository commandHistoryRepository;

    public CommandInvoker(CommandHistoryRepository commandHistoryRepository) {
        this.commandHistoryRepository = commandHistoryRepository;
    }

    /**
     * Execute a command, persist its description to the audit log, and push it to the undo stack.
     */
    public void execute(DisasterCommand command) {
        command.execute();

        // Persist to DB for permanent audit trail
        CommandHistory history = new CommandHistory(
                command.getCommandType(),
                command.getDescription(),
                "CommandCenter",
                null,
                null
        );
        commandHistoryRepository.save(history);

        // Keep in-memory undo stack bounded
        commandHistory.push(command);
        if (commandHistory.size() > MAX_HISTORY) {
            commandHistory.pollLast();
        }

        log.info("[CommandInvoker] Executed: {}", command.getDescription());
    }

    /**
     * Undo the most recently executed command.
     * @return description of undone command, or null if history empty
     */
    public String undo() {
        if (commandHistory.isEmpty()) {
            log.warn("[CommandInvoker] No commands to undo");
            return null;
        }
        DisasterCommand last = commandHistory.pop();
        last.undo();

        // Mark as undone in DB
        CommandHistory undoRecord = new CommandHistory(
                "UNDO_" + last.getCommandType(),
                "UNDONE: " + last.getDescription(),
                "CommandCenter",
                null,
                null
        );
        commandHistoryRepository.save(undoRecord);

        log.info("[CommandInvoker] Undone: {}", last.getDescription());
        return last.getDescription();
    }

    public boolean hasHistory() {
        return !commandHistory.isEmpty();
    }

    public int getHistorySize() {
        return commandHistory.size();
    }
}
