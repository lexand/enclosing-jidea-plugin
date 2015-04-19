package com.lexand.enclosing;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
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

    public void initComponent() {
        // insert component initialization logic here
    }

    public void disposeComponent() {
        // insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "Selection Enclose Plugin";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    public void moduleAdded() {
        EditorActionManager manager = EditorActionManager.getInstance();
        TypedAction typedAction = manager.getTypedAction();
        typedAction.setupHandler(new EncloseTypedAction(typedAction.getHandler()));
    }

    private class EncloseTypedAction implements TypedActionHandler {

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

                int[] selectionStarts = selectionModel.getBlockSelectionStarts();
                int[] selectionEnds = selectionModel.getBlockSelectionEnds();

                int counts = Math.min(
                        selectionStarts.length,
                        selectionEnds.length);

                for (int i = counts - 1; i >= 0; --i) {
                    document.insertString(selectionEnds[i], String.valueOf(paired));
                    document.insertString(selectionStarts[i], String.valueOf(charTyped));
                }

                caretModel.moveToOffset(selectionEnds[selectionEnds.length - 1] + counts * 2 - 1);

            }
            else {
                defaultHandler.execute(editor, charTyped, dataContext);
            }
        }
    }
}
