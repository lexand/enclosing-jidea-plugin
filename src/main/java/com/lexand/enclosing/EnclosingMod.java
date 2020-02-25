package com.lexand.enclosing;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Quotes
 *
 * @author alex
 * @date 30.04.13
 * @time 11:01
 * @package com.lexand.enclosing
 */
public class EnclosingMod implements ModuleComponent {
    public EnclosingMod(Module module) {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Selection Enclose Plugin";
    }

    @Override
    public void moduleAdded() {
        TypedAction typedAction = TypedAction.getInstance();
        typedAction.setupHandler(new EncloseTypedAction(typedAction.getHandler()));
    }

    private static class EncloseTypedAction implements TypedActionHandler {

        private final TypedActionHandler defaultHandler;
        private final char[] CHARS = {'"', '\'', '(', '[', '`', '{'};
        private final char[] PAIRED_CHARS = {'(', ')', '[', ']', '{', '}'};

        public EncloseTypedAction(TypedActionHandler handler) {
            defaultHandler = handler;
        }

        @Override
        public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
            Document document = editor.getDocument();

            SelectionModel selectionModel = editor.getSelectionModel();

            if (document.isWritable() &&
                    (Arrays.binarySearch(CHARS, charTyped) >= 0) &&
                    selectionModel.hasSelection()) {

                CaretModel caretModel = editor.getCaretModel();

                int pairInd = Arrays.binarySearch(PAIRED_CHARS, charTyped) + 1;
                char paired = (pairInd > 0) ? PAIRED_CHARS[pairInd] : charTyped;

                for (Caret caret : caretModel.getAllCarets()) {
                    if (caret.hasSelection()) {
                        document.insertString(caret.getSelectionStart(), String.valueOf(charTyped));
                        document.insertString(caret.getSelectionEnd(), String.valueOf(paired));
                    }
                }

            }
            else {
                defaultHandler.execute(editor, charTyped, dataContext);
            }
        }
    }
}
