package by.pizzzadog.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IncrementSelectionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        Document document = editor.getDocument();
        Project project = editor.getProject();
        editor.getCaretModel();

        List<Caret> allCarets = editor.getCaretModel().getAllCarets();
        AtomicInteger counter = new AtomicInteger();

        try {
            counter.set(Integer.parseInt(allCarets.get(0).getSelectedText()));
        } catch (NumberFormatException ex) {
            return;
        }

        for (Caret caret : allCarets) {
            try {
                Integer.parseInt(caret.getSelectedText());
                int start = caret.getSelectionStart();
                int end = caret.getSelectionEnd();
                WriteCommandAction.runWriteCommandAction(project, () ->
                        document.replaceString(start, end, String.valueOf(counter.getAndIncrement())));
            } catch (NumberFormatException ignored) {}
        }

        editor.getSelectionModel().removeSelection(true);
    }

    /**
     * Sets visibility and enables this action menu item if:
     * <ul>
     *   <li>a project is open</li>
     *   <li>an editor is active</li>
     *   <li>some characters are selected</li>
     * </ul>
     *
     * @param e Event related to this action
     */
    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Get required data keys
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        Document document = editor.getDocument();
        boolean hasSelection = editor.getSelectionModel().hasSelection();
        int starts = editor.getSelectionModel().getBlockSelectionStarts().length;
        int ends = editor.getSelectionModel().getBlockSelectionEnds().length;
        boolean selectionsEquals = starts == ends;
        boolean multiSelection = editor.getCaretModel().getAllCarets().size() > 1;

        // Set visibility and enable only in case of existing project and editor and if a selection exists
        boolean enabledAndVisible = multiSelection && hasSelection && selectionsEquals
                && document != null && editor != null;
        e.getPresentation().setEnabledAndVisible(enabledAndVisible);
    }
}
