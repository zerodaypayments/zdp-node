package io.zdp.node.storage.transfer.dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@Repository
public class CurrentTransferDaoImpl implements CurrentTransferDao {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private File currentTransfersFile;

	private FileWriter currentTransfersFileWriter;

	private Cache<String, CurrentTransfer> recentTransfers;

	@PostConstruct
	public void init() throws IOException {

		this.currentTransfersFile = new File(SystemUtils.USER_HOME, ".zdp" + File.separator + "data" + File.separator + "tx.dat");

		if (false == this.currentTransfersFile.exists()) {
			FileUtils.forceMkdirParent(this.currentTransfersFile);
		}

		log.debug("transfersFile: " + this.currentTransfersFile);

		this.currentTransfersFileWriter = new FileWriter(this.currentTransfersFile, true);

		this.recentTransfers = CacheBuilder.newBuilder().maximumSize(100000).build();

	}

	@PreDestroy
	public void close() {

		synchronized (currentTransfersFileWriter) {
			try {
				log.debug("Closing currentTransfersFileWriter");
				this.currentTransfersFileWriter.flush();
				this.currentTransfersFileWriter.close();
				log.debug("Closed currentTransfersFileWriter");
			} catch (IOException e) {
				log.error("Error: ", e);
			}
		}
	}

	@Override
	public void save(CurrentTransfer t) {

		if (this.recentTransfers.getIfPresent(t) == null) {

			synchronized (currentTransfersFileWriter) {

				try {

					currentTransfersFileWriter.write(t.toRecordString());
					currentTransfersFileWriter.write(IOUtils.LINE_SEPARATOR_UNIX);
					currentTransfersFileWriter.flush();

					recentTransfers.put(t.getUuid(), t);

					log.debug("Added transfer: " + t);

				} catch (IOException e) {
					log.error("Error: ", e);
				}

			}

		}

	}

}
