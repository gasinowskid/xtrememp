/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
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

import java.awt.event.KeyEvent;
import xtrememp.ui.textfield.SearchTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
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
import java.awt.event.KeyListener;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import org.apache.commons.io.FilenameUtils;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.utils.SubstanceStripingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.util.file.AudioFileFilter;
import xtrememp.util.file.M3uPlaylistFileFilter;
import xtrememp.util.file.PlaylistFileFilter;
import xtrememp.playlist.Playlist;
import xtrememp.playlist.PlaylistChangeListener;
import xtrememp.playlist.PlaylistException;
import xtrememp.playlist.PlaylistIO;
import xtrememp.playlist.PlaylistItem;
import xtrememp.util.AbstractSwingWorker;
import xtrememp.util.Utilities;
import xtrememp.util.file.XspfPlaylistFileFilter;

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
    private ExtendedJTable playlistTable;
    private JButton openPlaylistButton;
    private JButton savePlaylistButton;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JButton cleanPlaylistButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private ControlListener controlListener;
    private Playlist playlist;
    private PlaylistTableModel playlistTableModel;
    private SearchTextField searchWidget;
    private TableRowSorter<PlaylistTableModel> sorter;
    private RowFilter<PlaylistTableModel, Integer> searchFilter;
    private String searchString;
    private int doubleSelectedRow;
    private boolean firstPlaylistLoad = true;
    //@rom1dep :: Idea : add a JLabel somewhere to display the length of the pl (elements and duration)
    //              + make accessors
//    private int nbrElts;
//    private int listDuration;

    public PlaylistManager(ControlListener controlListener) {
        super(new BorderLayout());
        this.controlListener = controlListener;
        playlist = new Playlist();
        doubleSelectedRow = playlist.getCursorPosition();
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
        openPlaylistButton = new JButton(Utilities.getIcon("document-open.png"));
        openPlaylistButton.setToolTipText("Open playlist...");
        openPlaylistButton.addActionListener(this);
        toolBar.add(openPlaylistButton);
        savePlaylistButton = new JButton(Utilities.getIcon("document-save.png"));
        savePlaylistButton.setToolTipText("Save playlist...");
        savePlaylistButton.addActionListener(this);
        toolBar.add(savePlaylistButton);
        toolBar.addSeparator();
        addItemButton = new JButton(Utilities.getIcon("list-add.png"));
        addItemButton.setToolTipText("Add files or directories...");
        addItemButton.addActionListener(this);
        toolBar.add(addItemButton);
        removeItemButton = new JButton(Utilities.getIcon("list-remove.png"));
        removeItemButton.setToolTipText("Remove selected");
        removeItemButton.addActionListener(this);
        removeItemButton.setEnabled(false);
        toolBar.add(removeItemButton);
        cleanPlaylistButton = new JButton(Utilities.getIcon("edit-clear.png"));
        cleanPlaylistButton.setToolTipText("Clear playlist");
        cleanPlaylistButton.addActionListener(this);
        cleanPlaylistButton.setEnabled(false);
        toolBar.add(cleanPlaylistButton);
        toolBar.addSeparator();
        moveUpButton = new JButton(Utilities.getIcon("go-up.png"));
        moveUpButton.setToolTipText("Move up");
        moveUpButton.addActionListener(this);
        moveUpButton.setEnabled(false);
        toolBar.add(moveUpButton);
        moveDownButton = new JButton(Utilities.getIcon("go-down.png"));
        moveDownButton.setToolTipText("Move down");
        moveDownButton.addActionListener(this);
        moveDownButton.setEnabled(false);
        toolBar.add(moveDownButton);
        toolBar.add(Box.createHorizontalGlue());
        searchWidget = new SearchTextField(15);
        searchWidget.setMaximumSize(new Dimension(120, searchWidget.getPreferredSize().height));
        searchWidget.getTextField().getDocument().addDocumentListener(new SearchFilterListener());
        toolBar.add(searchWidget);
        toolBar.add(Box.createHorizontalStrut(4));
        add(toolBar, BorderLayout.NORTH);

        playlistTable = new ExtendedJTable(playlistTableModel);
        playlistTable.setDefaultRenderer(String.class, new PlaylistCellRenderer());
        playlistTable.setAutoCreateColumnsFromModel(true);
        playlistTable.setActionMap(null);
        //playlistTable.setTableHeader(null);
        playlistTable.setFillsViewportHeight(true);
        playlistTable.setShowGrid(false);
        playlistTable.setColumnSelectionAllowed(false);
        playlistTable.setRowSelectionAllowed(true);
        playlistTable.setIntercellSpacing(new Dimension(0, 0));
        playlistTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        playlistTable.putClientProperty(SubstanceLookAndFeel.WATERMARK_VISIBLE, true);
        playlistTable.addMouseListener(this);
        playlistTable.getSelectionModel().addListSelectionListener(this);
        playlistTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        playlistTable.setDropTarget(new DropTarget(playlistTable, this));
        playlistTable.getColumn(playlistTable.getColumnName(PlaylistTableModel.TRACK_COLUMN)).setMaxWidth(25);//Preferred doesn't work...
        playlistTable.getColumn(playlistTable.getColumnName(PlaylistTableModel.TIME_COLUMN)).setMaxWidth(60);
        playlistTable.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A && e.getModifiers() == KeyEvent.CTRL_MASK) {
                    playlistTable.selectAll();
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    remove();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });


        add(new JScrollPane(playlistTable), BorderLayout.CENTER);
    }

    //@TODO: Sorting on milti-criteria and/or regexp?
    protected void initSortingFiltering() {
        sorter = new TableRowSorter<PlaylistTableModel>(playlistTableModel);
        playlistTable.setRowSorter(sorter);
        searchFilter = new RowFilter<PlaylistTableModel, Integer>() {

            @Override
            public boolean include(Entry<? extends PlaylistTableModel, ? extends Integer> entry) {
                PlaylistTableModel playlistModel = entry.getModel();
                PlaylistItem pli = playlistModel.getPlaylistItem(entry.getIdentifier().intValue());
                boolean matches = false;
                String dname = pli.getFormattedDisplayName();
                String artist = pli.getTagInfo().getArtist();
                String album = pli.getTagInfo().getAlbum();
                if (dname != null && artist != null && album != null) {
                    matches = (dname.toLowerCase().contains(searchString.toLowerCase()) || artist.toLowerCase().contains(searchString.toLowerCase()) || album.toLowerCase().contains(searchString.toLowerCase()));
                }
                return matches;
            }
        };

        sorter.addRowSorterListener(playlistTable);
    }

    protected void addFiles(List<File> fileList) {
        AddFilesWorker addFilesWorker = new AddFilesWorker(fileList);
        addFilesWorker.execute();
        playlistTable.synchronize();
    }

    public void add(PlaylistItem pli) {
        playlistTableModel.add(pli);
    }

    public void add(List<PlaylistItem> newItems) {
        playlistTableModel.add(newItems);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void randomizePlaylist() {
        playlistTableModel.randomizePlaylist();
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

    public void showMediaInfoDialog() {
        int selectedRow = playlistTable.getSelectedRow();
        if (selectedRow != -1) {
            PlaylistItem pli = playlistTableModel.getPlaylistItem(playlistTable.convertRowIndexToModel(selectedRow));
            new MediaInfoDialog(pli);
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
            doubleSelectedRow = playlistTable.convertRowIndexToView(playlist.indexOf(pli));
            playlist.setCursor(doubleSelectedRow);
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
            doubleSelectedRow = playlistTable.convertRowIndexToView(playlist.indexOf(pli));
            playlist.setCursor(doubleSelectedRow);
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
            int itemIndex = playlist.indexOf(pli);
            if (itemIndex == -1) {
                //controlListener.acStop();
                controlListener.acDisable();
                if (!playlist.isEmpty()) {
                    playlist.begin();
                    //controlListener.acOpen();
                    doubleSelectedRow = playlistTable.convertRowIndexToView(0);
                }
            } else {
                playlist.setCursor(itemIndex);
                doubleSelectedRow = playlistTable.convertRowIndexToView(itemIndex);
            }
            clearSelection();
        }
    }

    private void clearSelection() {
        playlistTable.clearSelection();
        removeItemButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
    }

    public void clearPlaylist() {
        if (!playlist.isEmpty()) {
            playlistTableModel.clear();
            doubleSelectedRow = -1;
            removeItemButton.setEnabled(false);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            Settings.setPlaylistPosition(-1);
            //controlListener.acStop();
            controlListener.acDisable();
            //cleanPlaylistButton.setEnabled(false);
        } else {
            controlListener.acStop();
        }
    }

    public void colorizeRow() {
        if (!playlist.isEmpty()) {
            doubleSelectedRow = playlistTable.convertRowIndexToView(playlist.getCursorPosition());
            playlistTable.repaint();
            makeRowVisible(playlist.getCursorPosition());
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
        String oldSearchString = this.searchString;
        this.searchString = searchString;
        if (searchString != null && !searchString.isEmpty()) {
            sorter.setRowFilter(searchFilter);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
        } else {
            sorter.setRowFilter(null);
            moveUpButton.setEnabled(true);
            moveDownButton.setEnabled(true);
            colorizeRow();
        }
        firePropertyChange("searchString", oldSearchString, searchString);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == openPlaylistButton) {
            openPlaylist();
        } else if (source == savePlaylistButton) {
            savePlaylistDialog();
        } else if (source == addItemButton) {
            addFilesDialog();
        } else if (source == removeItemButton) {
            remove();
        } else if (source == cleanPlaylistButton) {
            clearPlaylist();
        } else if (source == moveUpButton) {
            moveUp();
        } else if (source == moveDownButton) {
            moveDown();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == playlistTable.getSelectionModel()) {
            if (playlistTable.getSelectedRowCount() > 0) {
                removeItemButton.setEnabled(true);
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
                clearSelection();
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

    protected class PlaylistCellRenderer extends DefaultTableCellRenderer {

        public PlaylistCellRenderer() {
            Font boldFont = this.getFont().deriveFont(Font.BOLD);
            this.setFont(boldFont);
//@moved l202, new impl is not that smart but faster alot
//            TableColumn column = playlistTable.getColumn(playlistTable.getColumnName(PlaylistTableModel.TIME_COLUMN));
//            JLabel label = new JLabel("XXX0:00:00");
//            column.setMinWidth(label.getPreferredSize().width);
//            column.setMaxWidth(label.getPreferredSize().width);
        }

        @Override//profiling XMP with a fullscreen frame says that 75% of the cpu is eaten while painting this JTable... :s
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
                return super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
            }

            SubstanceStripingUtils.applyStripedBackground(table, row, this);

            this.setText(String.valueOf(value));

            if (column == PlaylistTableModel.TIME_COLUMN) {
                this.setHorizontalAlignment(JLabel.RIGHT);
            } else {
                this.setHorizontalAlignment(JLabel.LEFT);
            }

            if (row == doubleSelectedRow) {
                this.setForeground(Color.red);
            } else {
                this.setForeground(table.getForeground());
            }

            this.setOpaque(false);
            return this;
        }

        @Override
        public final void paint(Graphics g) {
            super.paint(g);
        }

        @Override
        protected final void paintComponent(Graphics g) {
            super.paintComponent(g);
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
            }
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
//            if (playlist.getCursorPosition() == -1) {
//                playlist.begin();
//                controlListener.acOpen();
//            }
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

    class ExtendedJTable extends JTable implements PlaylistChangeListener {

        public ExtendedJTable(AbstractTableModel model) {
            super(model);
            playlist.addPlaylistChangeListener(this);
        }

        @Override
        /*
         * Called when user clicks on a table header
         */
        public void sorterChanged(final RowSorterEvent e) {
            if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
                synchronize();
            } else {
                super.sorterChanged(e);
            }
        }

        @Override
        public void synchronize() {
            logger.info("Syncronizing Playlist...");
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    //Resync model & view
                    int[] newSet = new int[getRowCount()];
                    for (int i = 0; i < getRowCount(); i++) {
                        newSet[i] = convertRowIndexToModel(i);
                    }
                    playlist.resort(newSet);
                    playlistTableModel.fireTableDataChanged();
                    makeRowVisible(playlist.getCursorPosition());
                    playlistTable.setEditingRow(playlist.getCursorPosition());//@Test
                    //playlistTableModel.fireTableRowsUpdated(1, playlistTable.getRowCount()-1);
                }
            });
        }
    }
}
