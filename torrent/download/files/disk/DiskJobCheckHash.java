package torrent.download.files.disk;

import torrent.TorrentException;
import torrent.download.Torrent;

/**
 * A job to check the hash of a piece for a given torrent
 * @author Johnnei
 *
 */
public class DiskJobCheckHash extends DiskJob {
	
	/**
	 * The piece to check the has for
	 */
	private int pieceIndex;
	
	public DiskJobCheckHash(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	@Override
	public void process(Torrent torrent) {
		try {
			if (torrent.getFiles().getPiece(pieceIndex).checkHash()) {
				if(torrent.getDownloadStatus() == Torrent.STATE_DOWNLOAD_DATA) {
					torrent.broadcastHave(pieceIndex);
				}
				torrent.log("Recieved and verified piece: " + pieceIndex);
				String p = Double.toString(torrent.getProgress());
				torrent.log("Torrent Progress: " + p.substring(0, (p.length() < 4) ? p.length() : 4) + "%");
			} else {
				torrent.log("Hash check failed on piece: " + pieceIndex, true);
				torrent.getFiles().getPiece(pieceIndex).hashFail();
			}
		} catch (TorrentException e) {
			torrent.log("Hash check error on piece: " + pieceIndex + ", Err: " + e.getMessage(), true);
			torrent.getFiles().getPiece(pieceIndex).hashFail();
		}
		torrent.addToHaltingOperations(-1);
	}

	@Override
	public int getPriority() {
		return HIGH;
	}

}
