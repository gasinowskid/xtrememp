/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2010 Besmir Beqiri
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp;

import xtrememp.ui.textfield.SearchTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import org.apache.commons.io.FilenameUtils;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.player.audio.AudioPlayer;
import xtrememp.util.file.AudioFileFilter;
import xtrememp.util.file.M3uPlaylistFileFilter;
import xtrememp.util.file.PlaylistFileFilter;
import xtrememp.playlist.Playlist;
import xtrememp.playlist.PlaylistException;
import xtrememp.playlist.PlaylistIO;
import xtrememp.playlist.PlaylistItem;
import xtrememp.util.AbstractSwingWorker;
import xtrememp.util.Utilities;
import xtrememp.util.file.XspfPlaylistFileFilter;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 *
 * Special thanks to rom1dep for the changes applied to this class.
 */
public class PlaylistManager extends JPanel implements ActionListener,
        DropTargetListener, ListSelectionListener, MouseListener {

    private final Logger logger = LoggerFactory.getLogger(PlaylistManager.class);
    private final AudioFileFilter audioFileFilter = new AudioFileFilter();
    private final PlaylistFileFilter playlistFileFilter = new PlaylistFileFilter();
    private JTable playlistTable;
    private JButton openPlaylistButton;
    private JButton savePlaylistButton;
    private JButton addToPlaylistButton;
    private JButton remFromPlaylistButton;
    private JButton cleanPlaylistButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton mediaInfoButton;
    private ControlListener controlListener;
    private Playlist playlist;
    private PlaylistTableModel playlistTableModel;
    private SearchTextField searchWidget;
    private TableRowSorter<PlaylistTableModel> tableRowSorter;
    private RowFilter<PlaylistTableModel, Integer> searchFilter;
    private String searchString;
    private int doubleSelectedRow = -1;
    private boolean firstPlaylistLoad = true;

    public PlaylistManager(ControlListener controlListener) {
        super(new BorderLayout());
        this.controlListener = controlListener;
        playlist = new Playlist();
        initModel();
        initComponents();
        initSortingFiltering();
    }

    protected void initModel() {
        playlistTableModel = new PlaylistTableModel(playlist);
    }

    protected void initComponents() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        openPlaylistButton = new JButton(Utilities.DOCUMENT_OPEN_ICON);
        openPlaylistButton.setToolTipText(tr("MainFrame.PlaylistManager.OpenPlaylist"));
        openPlaylistButton.addActionListener(this);
        toolBar.add(openPlaylistButton);
        savePlaylistButton = new JButton(Utilities.DOCUMENT_SAVE_ICON);
        savePlaylistButton.setToolTipText(tr("MainFrame.PlaylistManager.SavePlaylist"));
        savePlaylistButton.addActionListener(this);
        toolBar.add(savePlaylistButton);
        toolBar.addSeparator();
        addToPlaylistButton = new JButton(Utilities.LIST_ADD_ICON);
        addToPlaylistButton.setToolTipText(tr("MainFrame.PlaylistManager.AddToPlaylist"));
        addToPlaylistButton.addActionListener(this);
        toolBar.add(addToPlaylistButton);
        remFromPlaylistButton = new JButton(Utilities.LIST_REMOVE_ICON);
        remFromPlaylistButton.setToolTipText(tr("MainFrame.PlaylistManager.RemoveFromPlaylist"));
        remFromPlaylistButton.addActionListener(this);
        remFromPlaylistButton.setEnabled(false);
        toolBar.add(remFromPlaylistButton);
        cleanPlaylistButton = new JButton(Utilities.EDIT_CLEAR_ICON);
        cleanPlaylistButton.setToolTipText(tr("MainFrame.PlaylistManager.CleanPlaylist"));
        cleanPlaylistButton.addActionListener(this);
        cleanPlaylistButton.setEnabled(false);
        toolBar.add(cleanPlaylistButton);
        toolBar.addSeparator();
        moveUpButton = new JButton(Utilities.GO_UP_ICON);
        moveUpButton.setToolTipText(tr("MainFrame.PlaylistManager.MoveUp"));
        moveUpButton.addActionListener(this);
        moveUpButton.setEnabled(false);
        toolBar.add(moveUpButton);
        moveDownButton = new JButton(Utilities.GO_DOWN_ICON);
        moveDownButton.setToolTipText(tr("MainFrame.PlaylistManager.MoveDown"));
        moveDownButton.addActionListener(this);
        moveDownButton.setEnabled(false);
        toolBar.add(moveDownButton);
        toolBar.addSeparator();
        mediaInfoButton = new JButton(Utilities.MEDIA_INFO_ICON);
        mediaInfoButton.setToolTipText(tr("MainFrame.PlaylistManager.MediaInfo"));
        mediaInfoButton.addActionListener(this);
        mediaInfoButton.setEnabled(false);
        toolBar.add(mediaInfoButton);
        toolBar.add(Box.createHorizontalGlue());
        searchWidget = new SearchTextField(15);
        searchWidget.setMaximumSize(new Dimension(120, searchWidget.getPreferredSize().height));
        searchWidget.getTextField().getDocument().addDocumentListener(new SearchFilterListener());
        toolBar.add(searchWidget);
        toolBar.add(Box.createHorizontalStrut(6));
        this.add(toolBar, BorderLayout.NORTH);

        playlistTable = new JTable(playlistTableModel);
        playlistTable.setDefaultRenderer(String.class, new PlaylistCellRenderer());
        playlistTable.setActionMap(null);
        playlistTable.setTableHeader(null);
        playlistTable.setFillsViewportHeight(true);
        playlistTable.setShowGrid(false);
        playlistTable.setRowSelectionAllowed(true);
        playlistTable.setColumnSelectionAllowed(false);
        playlistTable.setDragEnabled(false);
        playlistTable.setFont(playlistTable.getFont().deriveFont(Font.BOLD));
        playlistTable.setIntercellSpacing(new Dimension(0, 0));
        playlistTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        playlistTable.addMouseListener(this);
        playlistTable.getSelectionModel().addListSelectionListener(this);
        playlistTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        playlistTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                // Select All Ctrl + A
                if (e.getKeyCode() == KeyEvent.VK_A && e.getModifiers() == KeyEvent.CTRL_MASK) {
                    playlistTable.selectAll();
                } // Select previous row
                else if (e.getKeyCode() == KeyEvent.VK_UP && playlistTable.getSelectedRow() > 0) {
                    int previousRowIndex = playlistTable.getSelectedRow() - 1;
                    playlistTable.clearSelection();
                    playlistTable.addRowSelectionInterval(previousRowIndex, previousRowIndex);
                    makeRowVisible(previousRowIndex);
                } // Select next row
                else if (e.getKeyCode() == KeyEvent.VK_DOWN && playlistTable.getSelectedRow() < playlistTable.getRowCount() - 1) {
                    int nextRowIndex = playlistTable.getSelectedRow() + 1;
                    playlistTable.clearSelection();
                    playlistTable.addRowSelectionInterval(nextRowIndex, nextRowIndex);
                    makeRowVisible(nextRowIndex);
                } //
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = playlistTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedRow = playlistTable.convertRowIndexToModel(selectedRow);
                        playlist.setCursor(selectedRow);
                        controlListener.acOpenAndPlay();
                    }
                }
//                else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//                    remove();
//                }
            }
        });
        XtremeMP.getInstance().getMainFrame().setDropTarget(new DropTarget(playlistTable, this));
        JScrollPane ptScrollPane = new JScrollPane(playlistTable);
        ptScrollPane.setActionMap(null);
        this.add(ptScrollPane, BorderLayout.CENTER);
    }

    protected void initSortingFiltering() {
        tableRowSorter = new TableRowSorter<PlaylistTableModel>(playlistTableModel);
        playlistTable.setRowSorter(tableRowSorter);
        searchFilter = new RowFilter<PlaylistTableModel, Integer>() {

            @Override
            public boolean include(Entry<? extends PlaylistTableModel, ? extends Integer> entry) {
                PlaylistTableModel playlistModel = entry.getModel();
                PlaylistItem pli = playlistModel.getPlaylistItem(entry.getIdentifier().intValue());
                boolean matches = false;
                String titleAndArtist = pli.getFormattedDisplayName();
                if (titleAndArtist != null) {
                    matches = titleAndArtist.toLowerCase().contains(searchString.toLowerCase());
                }
                return matches;
            }
        };
    }

    protected void addFiles(List<File> newFiles) {
        AddFilesWorker addFilesWorker = new AddFilesWorker(newFiles);
        addFilesWorker.execute();
    }

    public void add(PlaylistItem newPli) {
        playlistTableModel.add(newPli);
    }

    public void add(List<PlaylistItem> newItems) {
        playlistTableModel.add(newItems);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void randomizePlaylist() {
        if (!playlist.isEmpty()) {
            PlaylistItem pli = playlist.getCursor();
            playlistTableModel.randomizePlaylist();
            playlist.setCursor(playlist.indexOf(pli));
            colorizeRow();
        }
    }

    public void loadPlaylist(String location) {
        PlaylistLoaderWorker playlistLoader = new PlaylistLoaderWorker(location);
        playlistLoader.execute();
    }

    public void refreshRow(int index) {
        playlistTableModel.fireTableRowsUpdated(index, index);
    }

    public void openPlaylist() {
        JFileChooser fileChooser = new JFileChooser(Settings.getLastDir());
        fileChooser.addChoosableFileFilter(playlistFileFilter);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Settings.setLastDir(file.getPath());
            clearPlaylist();
            loadPlaylist(file.getPath());
        }
    }

    public boolean savePlaylistDialog() {
        JFileChooser fileChooser = new JFileChooser(Settings.getLastDir());
        M3uPlaylistFileFilter m3uFileFilter = new M3uPlaylistFileFilter();
        XspfPlaylistFileFilter xspfFileFilter = new XspfPlaylistFileFilter();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(m3uFileFilter);
        fileChooser.addChoosableFileFilter(xspfFileFilter);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileFilter fileFilter = fileChooser.getFileFilter();
            String fileName = file.getName().toLowerCase();
            if (fileFilter == m3uFileFilter) {
                if (!fileName.endsWith(".m3u")) {
                    fileName = fileName.concat(".m3u");
                }
                try {
                    return PlaylistIO.saveM3U(playlist, file.getParent() + File.separator + fileName);
                } catch (PlaylistException ex) {
                    logger.error("Can't save playlist in M3U format", ex);
                }
            }
            if (fileFilter == xspfFileFilter) {
                if (!fileName.endsWith(".xspf")) {
                    fileName = fileName.concat(".xspf");
                }
                try {
                    return PlaylistIO.saveXSPF(playlist, file.getParent() + File.separator + fileName);
                } catch (PlaylistException ex) {
                    logger.error("Can't save playlist in XSPF format", ex);
                }
            }
            Settings.setLastDir(file.getParent());
        }
        return false;
    }

    public void addFilesDialog() {
        JFileChooser fileChooser = new JFileChooser(Settings.getLastDir());
        fileChooser.setDialogTitle("Add Files or Directories");
        fileChooser.addChoosableFileFilter(audioFileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Settings.setLastDir(fileChooser.getSelectedFiles()[0].getParent());
            addFiles(Arrays.asList(fileChooser.getSelectedFiles()));
        }
    }

    public void moveUp() {
        if (playlistTable.getSelectedRowCount() > 0) {
            PlaylistItem pli = playlist.getCursor();
            int[] selectedRows = playlistTable.getSelectedRows();
            int minSelectedIndex = selectedRows[0];
            if (minSelectedIndex > 0) {
                playlistTable.clearSelection();
                for (int i = 0, len = selectedRows.length; i < len; i++) {
                    int selectedRow = selectedRows[i];
                    playlistTableModel.moveRow(selectedRow, selectedRow, selectedRow - 1);
                    playlistTable.addRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
                makeRowVisible(minSelectedIndex - 1);
            }
            playlist.setCursor(playlist.indexOf(pli));
            colorizeRow();
        }
    }

    public void moveDown() {
        if (playlistTable.getSelectedRowCount() > 0) {
            PlaylistItem pli = playlist.getCursor();
            int[] selectedRows = playlistTable.getSelectedRows();
            int maxLength = selectedRows.length - 1;
            int maxSelectedIndex = selectedRows[maxLength];
            if (maxSelectedIndex < playlist.size() - 1) {
                playlistTable.clearSelection();
                for (int i = maxLength; i >= 0; i--) {
                    int selectedRow = selectedRows[i];
                    playlistTableModel.moveRow(selectedRow, selectedRow, selectedRow + 1);
                    playlistTable.addRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                }
                makeRowVisible(maxSelectedIndex + 1);
            }
            playlist.setCursor(playlist.indexOf(pli));
            colorizeRow();
        }
    }

    public void remove() {
        if (playlistTable.getSelectedRowCount() > 0) {
            PlaylistItem pli = playlist.getCursor();
            Vector<PlaylistItem> items = new Vector<PlaylistItem>();
            int[] selectedRows = playlistTable.getSelectedRows();
            for (int i = 0, len = selectedRows.length; i < len; i++) {
                selectedRows[i] = playlistTable.convertRowIndexToModel(selectedRows[i]);
                items.add(playlist.getItemAt(selectedRows[i]));
            }
            playlistTableModel.removeAll(items);
            if (playlist.isEmpty()) {
                cleanPlaylistButton.setEnabled(false);
            }
            playlist.setCursor(playlist.indexOf(pli));
            clearSelection();
            colorizeRow();
        }
    }

    protected void clearSelection() {
        playlistTable.clearSelection();
        remFromPlaylistButton.setEnabled(false);
        mediaInfoButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
    }

    public void clearPlaylist() {
        if (!playlist.isEmpty()) {
            playlistTableModel.clear();
            doubleSelectedRow = -1;
            remFromPlaylistButton.setEnabled(false);
            mediaInfoButton.setEnabled(false);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            Settings.setPlaylistPosition(-1);
            cleanPlaylistButton.setEnabled(false);
            playlistTable.requestFocusInWindow();
        }
    }

    public void colorizeRow() {
        if (!playlist.isEmpty()) {
            int cursorPos = playlist.getCursorPosition();
            doubleSelectedRow = (cursorPos == -1) ? -1 : playlistTable.convertRowIndexToView(cursorPos);
            playlistTable.repaint();
            makeRowVisible(cursorPos);
        }
    }

    public void makeRowVisible(int rowIndex) {
        if (!(playlistTable.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) playlistTable.getParent();
        Rectangle contentRect = (Rectangle) playlistTable.getCellRect(rowIndex, playlistTable.getSelectedColumn(), true).clone();
        Point pt = viewport.getViewPosition();
        contentRect.setLocation(contentRect.x - pt.x, contentRect.y - pt.y);
        viewport.scrollRectToVisible(contentRect);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        if (searchString != null && !searchString.isEmpty()) {
            tableRowSorter.setRowFilter(searchFilter);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
        } else {
            tableRowSorter.setRowFilter(null);
            moveUpButton.setEnabled(true);
            moveDownButton.setEnabled(true);
        }
        colorizeRow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(openPlaylistButton)) {
            openPlaylist();
        } else if (source.equals(savePlaylistButton)) {
            savePlaylistDialog();
        } else if (source.equals(addToPlaylistButton)) {
            addFilesDialog();
        } else if (source.equals(remFromPlaylistButton)) {
            remove();
        } else if (source.equals(cleanPlaylistButton)) {
            clearPlaylist();
        } else if (source.equals(moveUpButton)) {
            moveUp();
        } else if (source.equals(moveDownButton)) {
            moveDown();
        } else if (source.equals(mediaInfoButton)) {
            int selectedRow = playlistTable.getSelectedRow();
            if (selectedRow != -1) {
                PlaylistItem pli = playlistTableModel.getPlaylistItem(playlistTable.convertRowIndexToModel(selectedRow));
                MediaInfoWorker mediaInfoWorker = new MediaInfoWorker(pli);
                mediaInfoWorker.execute();
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == playlistTable.getSelectionModel()) {
            if (playlistTable.getSelectedRowCount() > 0) {
                remFromPlaylistButton.setEnabled(true);
                mediaInfoButton.setEnabled(true);
            }
            ListSelectionModel lsm = playlistTable.getSelectionModel();
            if (lsm.getMinSelectionIndex() == 0) {
                moveUpButton.setEnabled(false);
            } else {
                moveUpButton.setEnabled(true);
            }
            if (lsm.getMaxSelectionIndex() == (playlistTable.getRowCount() - 1)) {
                moveDownButton.setEnabled(false);
            } else {
                moveDownButton.setEnabled(true);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent ev) {
        DropTargetContext targetContext = ev.getDropTargetContext();
        Transferable t = ev.getTransferable();
        try {
            // Windows
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                ev.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                addFiles((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
                targetContext.dropComplete(true);
                // Linux
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                ev.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String urls = (String) t.getTransferData(DataFlavor.stringFlavor);
                List<File> fileList = new ArrayList<File>();
                StringTokenizer st = new StringTokenizer(urls);
                while (st.hasMoreTokens()) {
                    URI uri = new URI(st.nextToken());
                    fileList.add(new File(uri));
                }
                addFiles(fileList);
                targetContext.dropComplete(true);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent ev) {
    }

    @Override
    public void dragOver(DropTargetDragEvent ev) {
    }

    @Override
    public void dragEnter(DropTargetDragEvent ev) {
    }

    @Override
    public void dragExit(DropTargetEvent ev) {
    }

    @Override
    public void mouseClicked(MouseEvent ev) {
        int selectedRow = playlistTable.rowAtPoint(ev.getPoint());
        if (SwingUtilities.isLeftMouseButton(ev) && ev.getClickCount() == 2) {
            if (selectedRow != -1) {
                selectedRow = playlistTable.convertRowIndexToModel(selectedRow);
                playlist.setCursor(selectedRow);
                controlListener.acOpenAndPlay();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent ev) {
    }

    @Override
    public void mouseExited(MouseEvent ev) {
    }

    @Override
    public void mouseEntered(MouseEvent ev) {
    }

    @Override
    public void mouseReleased(MouseEvent ev) {
    }

    protected class PlaylistCellRenderer extends SubstanceDefaultTableCellRenderer {

        private Border emptyBorder = BorderFactory.createEmptyBorder();
        private Color dsColor = Color.red;

        public PlaylistCellRenderer() {
            super();
            TableColumn column = playlistTable.getColumn(playlistTable.getColumnName(PlaylistTableModel.TIME_COLUMN));
            JLabel label = new JLabel("XXX0:00:00");
            int labelWidth = label.getPreferredSize().width;
            column.setMinWidth(labelWidth);
            column.setMaxWidth(labelWidth);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
//            if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
//                return super.getTableCellRendererComponent(table, value,
//                        isSelected, hasFocus, row, column);
//            }

            super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            this.setBorder(emptyBorder);

            if (column == PlaylistTableModel.TIME_COLUMN) {
                this.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                this.setHorizontalAlignment(SwingConstants.LEFT);
            }

            if (row == doubleSelectedRow) {
                this.setForeground(dsColor);
            }

            return this;
        }
    }

    protected class SearchFilterListener implements DocumentListener {

        public void changeFilter(DocumentEvent event) {
            Document document = event.getDocument();
            try {
                clearSelection();
                setSearchString(document.getText(0, document.getLength()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changeFilter(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changeFilter(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changeFilter(e);
        }
    }

    protected class PlaylistLoaderWorker extends AbstractSwingWorker<Void, PlaylistItem> {

        private final String location;

        public PlaylistLoaderWorker(String location) {
            this.location = location;
        }

        @Override
        protected Void doInBackground() throws Exception {
            List<PlaylistItem> pliList = PlaylistIO.load(location);
            int count = 0;
            int size = pliList.size();
            for (PlaylistItem pli : pliList) {
                if (pli.isFile()) {
                    pli.loadTagInfo();
                }
                publish(pli);
                count++;
                setProgress(100 * count / size);
            }
            return null;
        }

        @Override
        protected void process(List<PlaylistItem> moreItems) {
            playlistTableModel.add(moreItems);
        }

        @Override
        protected void done() {
            setProgress(100);
            if (!playlist.isEmpty()) {
                cleanPlaylistButton.setEnabled(true);
                AudioPlayer audioPlayer = XtremeMP.getInstance().getAudioPlayer();
                if (audioPlayer.getState() == AudioSystem.NOT_SPECIFIED || audioPlayer.getState() == AudioPlayer.STOP) {
                    int index = Settings.getPlaylistPosition();
                    if (firstPlaylistLoad && index >= 0 && index <= (playlist.size() - 1)) {
                        playlist.setCursor(index);
                    } else {
                        playlist.begin();
                    }
                    if (firstPlaylistLoad) {
                        firstPlaylistLoad = false;
                        controlListener.acOpen();
                    } else {
                        controlListener.acOpenAndPlay();
                    }
                }
            }
        }
    }

    protected class AddFilesWorker extends AbstractSwingWorker<Void, PlaylistItem> {

        private final List<File> fileList;

        public AddFilesWorker(List<File> fileList) {
            this.fileList = fileList;
        }

        @Override
        protected Void doInBackground() {
            List<File> tempFileList = new ArrayList<File>();
            for (File file : fileList) {
                if (file.isDirectory()) {
                    scanDir(file, tempFileList);
                } else if (audioFileFilter.accept(file)) {
                    tempFileList.add(file);
                }
            }
            int count = 0;
            int size = tempFileList.size();
            for (File file : tempFileList) {
                String baseName = FilenameUtils.getBaseName(file.getName());
                PlaylistItem pli = new PlaylistItem(baseName, file.getAbsolutePath(), -1, true);
                pli.loadTagInfo();
                publish(pli);
                count++;
                setProgress(100 * count / size);
            }
            return null;
        }

        @Override
        protected void process(List<PlaylistItem> moreItems) {
            playlistTableModel.add(moreItems);
        }

        @Override
        protected void done() {
            setProgress(100);
            if (!playlist.isEmpty()) {
                cleanPlaylistButton.setEnabled(true);
            }
        }

        protected void scanDir(File dir, List<File> fileList) {
            for (File file : dir.listFiles((FilenameFilter) audioFileFilter)) {
                if (file.isFile()) {
                    fileList.add(file);
                } else {
                    scanDir(file, fileList);
                }
            }
        }
    }

    protected class MediaInfoWorker extends AbstractSwingWorker<Void, PlaylistItem> {

        private final PlaylistItem pli;

        public MediaInfoWorker(PlaylistItem pli) {
            this.pli = pli;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (pli != null && pli.getTagInfo() == null) {
                pli.loadTagInfo();
            }
            return null;
        }

        @Override
        protected void process(List<PlaylistItem> moreItems) {
            playlistTableModel.add(moreItems);
        }

        @Override
        protected void done() {
            setProgress(100);
            if (pli != null) {
                new MediaInfoDialog(pli);
            }
        }
    }
}
