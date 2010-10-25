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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import xtrememp.playlist.PlaylistItem;
import xtrememp.tag.TagInfo;
import xtrememp.util.Utilities;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class MediaInfoDialog extends JDialog implements ActionListener {

    private JTextField locationTextField;
    private JLabel titleLabel;
    private JTextField titleTextField;
    private JLabel artistLabel;
    private JTextField artistTextField;
    private JLabel albumLabel;
    private JTextField albumTextField;
    private JLabel genreLabel;
    private JTextField genreTextField;
    private JLabel trackLabel;
    private JTextField trackTextField;
    private JLabel yearLabel;
    private JTextField yearTextField;
    private JLabel commentLabel;
    private JTextArea commentTextArea;
    private JLabel cdLabel;
    private JButton closeButton;

    public MediaInfoDialog(PlaylistItem pli) {
        super(XtremeMP.getInstance().getMainFrame(), true);
        setLayout(new MigLayout("fill"));
        setTitle(tr("Dialog.MediaInformation"));
        initComponents();
        Utilities.closeOnEscape(this);

        locationTextField.setText(pli.getLocation());
        locationTextField.setToolTipText(pli.getLocation());
        TagInfo tagInfo = pli.getTagInfo();
        if (tagInfo != null) {
            String title = tagInfo.getTitle();
            if (!Utilities.isNullOrEmpty(title)) {
                titleTextField.setText(title.trim());
            }
            String artist = tagInfo.getArtist();
            if (!Utilities.isNullOrEmpty(artist)) {
                artistTextField.setText(artist.trim());
            }
            String album = tagInfo.getAlbum();
            if (!Utilities.isNullOrEmpty(album)) {
                albumTextField.setText(album.trim());
            }
            String genre = tagInfo.getGenre();
            if (!Utilities.isNullOrEmpty(genre)) {
                genreTextField.setText(genre.trim());
            }
            String comment = tagInfo.getComment();
            if (!Utilities.isNullOrEmpty(comment)) {
                commentTextArea.setText(comment.trim());
            }

            int track = -1;
            try {
//                 Certain tags are in the form of track number/total number of tracks
                String trackString = String.valueOf(tagInfo.getTrack());
                if (trackString.contains("/")) {
                    int separatorPosition = trackString.indexOf("/");
                    track = Integer.parseInt(trackString.substring(0, separatorPosition));
                } else {
                    track = Integer.parseInt(trackString);
                }
            } catch (NumberFormatException ex) {
                track = -1;
            } finally {
                if (track != -1) {
                    trackTextField.setText(String.valueOf(track));
                }
            }

            int year = -1;
            try {
                String yearString = tagInfo.getYear();
                if (yearString != null && !yearString.isEmpty()) {
                    year = Integer.parseInt(yearString);
                }
            } catch (NumberFormatException ex) {
                year = -1;
            } finally {
                if (year != -1) {
                    yearTextField.setText(String.valueOf(year));
                }
            }
            cdLabel.setText(tagInfo.getCodecDetails());
        }

        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
        getRootPane().setDefaultButton(closeButton);
        closeButton.requestFocusInWindow();
    }

    private void initComponents() {
        Container container = getContentPane();
        JPanel northPanel = new JPanel(new MigLayout("wrap", "[right,5lp:pref][500]", ""));
        northPanel.add(new JLabel(tr("Dialog.MediaInformation.Location")));
        locationTextField = new JTextField();
        locationTextField.setEditable(false);
        northPanel.add(locationTextField, "growx,push");
        container.add(northPanel, "north");

        JPanel buttonPanel = new JPanel(new MigLayout("nogrid, fillx, aligny 100%, gapy unrel"));
        closeButton = new JButton(tr("Button.Close"));
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton, "tag cancel");
        container.add(buttonPanel, "south");

        JPanel centerPanel = new JPanel(new MigLayout("", "[trailing][grow,fill]", ""));
        centerPanel.setBorder(BorderFactory.createTitledBorder(tr("Dialog.MediaInformation.StandardTags")));
        titleLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Title"));
        centerPanel.add(titleLabel);
        titleTextField = new JTextField();
        centerPanel.add(titleTextField, "span,growx");
        artistLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Artist"));
        centerPanel.add(artistLabel);
        artistTextField = new JTextField();
        centerPanel.add(artistTextField, "span,growx");
        albumLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Album"));
        centerPanel.add(albumLabel);
        albumTextField = new JTextField();
        centerPanel.add(albumTextField, "span,growx");
        genreLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Genre"));
        centerPanel.add(genreLabel);
        genreTextField = new JTextField();
        centerPanel.add(genreTextField, "growx,width 100:null:null");
        trackLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Track"));
        centerPanel.add(trackLabel, "split");
        trackTextField = new JTextField();
        centerPanel.add(trackTextField, "growx,width 35:null:null");
        yearLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Year"));
        centerPanel.add(yearLabel, "split");
        yearTextField = new JTextField();
        centerPanel.add(yearTextField, "growx,wrap,width 45:null:null");
        commentLabel = new JLabel(tr("Dialog.MediaInformation.StandardTags.Comment"));
        centerPanel.add(commentLabel);
        commentTextArea = new JTextArea();
        centerPanel.add(new JScrollPane(commentTextArea), "span, growx, width min:150, height min:100");
        container.add(centerPanel, "spany, grow, center");

        JPanel eastPanel = new JPanel(new MigLayout("fill", "[leading]", "[top]"));
        eastPanel.setBorder(BorderFactory.createTitledBorder(tr("Dialog.MediaInformation.CodecDetails")));
        cdLabel = new JLabel();
        eastPanel.add(cdLabel, "width 150:null:null");
        container.add(eastPanel, "east");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == closeButton) {
            dispose();
        }
    }
}
