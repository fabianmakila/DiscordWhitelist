package fi.fabianadrian.discordwhitelist.common.command;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.exception.*;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.incendo.cloud.exception.handling.ExceptionController;
import org.incendo.cloud.util.TypeUtils;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;

public abstract class AbstractExceptionHandler<C> {
	private final Logger logger;

	public AbstractExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	public void register(CommandManager<C> manager) {
		ExceptionController<C> controller = manager.exceptionController();
		controller.registerHandler(Throwable.class, context -> {
			final StringWriter writer = new StringWriter();
			context.exception().printStackTrace(new PrintWriter(writer));
			final String stackTrace = writer.toString().replaceAll("\t", "    ");

			send(context, Caption.of("exception.unexpected"), CaptionVariable.of("stacktrace", stackTrace));
			this.logger.error("An unhandled exception was thrown during command execution", context.exception());
		});
		controller.registerHandler(CommandExecutionException.class, context -> {
			final Throwable cause = context.exception().getCause();

			final StringWriter writer = new StringWriter();
			cause.printStackTrace(new PrintWriter(writer));
			final String stackTrace = writer.toString().replaceAll("\t", "    ");

			send(context, Caption.of("exception.unexpected"), CaptionVariable.of("stacktrace", stackTrace));
			this.logger.error("Exception executing command handler", cause);
		});
		controller.registerHandler(ArgumentParseException.class, context -> {
			String message = context.exception().getCause().getMessage();
			send(context, Caption.of("exception.invalid-argument"), CaptionVariable.of("message", message));
		});
		controller.registerHandler(NoSuchCommandException.class, context ->
				send(context, Caption.of("exception.no-such-command"), CaptionVariable.of("command", context.exception().suppliedCommand()))
		);
		controller.registerHandler(NoPermissionException.class, context -> {
			String permission = context.exception().permissionResult().permission().permissionString();
			send(context, Caption.of("exception.no-permission"), CaptionVariable.of("permission", permission));
		});
		controller.registerHandler(InvalidCommandSenderException.class, context -> {
			final boolean multiple = context.exception().requiredSenderTypes().size() != 1;
			final String expected = multiple
					? context.exception().requiredSenderTypes().stream().map(TypeUtils::simpleName)
					.collect(Collectors.joining(", "))
					: TypeUtils.simpleName(context.exception().requiredSenderTypes().iterator().next());
			send(context, multiple ? Caption.of("exception.invalid-sender-list") : Caption.of("exception.invalid-sender"), CaptionVariable.of("expected", expected));
		});
		controller.registerHandler(InvalidSyntaxException.class, context ->
				send(context, Caption.of("exception.invalid-syntax"), CaptionVariable.of("syntax", context.exception().correctSyntax()))
		);
	}

	protected abstract void send(ExceptionContext<C, ?> context, Caption caption, CaptionVariable... variables);
}
