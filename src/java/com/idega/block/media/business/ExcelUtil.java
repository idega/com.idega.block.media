package com.idega.block.media.business;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelUtil {

	private static final ExcelUtil UTIL = new ExcelUtil();

	private ExcelUtil() {}

	public static final ExcelUtil getInstance() {
		return UTIL;
	}

	public boolean autosizeSheetColumns(Sheet sheet, int nrOfCells) {
		if (sheet == null || nrOfCells <= 0) {
			return false;
		}

		for (int column = 0; column < nrOfCells; column++) {
			try {
				sheet.autoSizeColumn(column);
			} catch (Exception e) {}
		}

		return true;
	}

	public boolean autosizeColumns(Sheet sheet) {
		if (sheet == null) {
			return Boolean.FALSE;
		}

		short lastCell = 0;
		for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext();) {
			Row row = rowIterator.next();
			if (row == null) {
				continue;
			}

			short cellNum = row.getLastCellNum();
			lastCell = lastCell < cellNum ? cellNum : lastCell;
		}

		return autosizeSheetColumns(sheet, lastCell);
	}

}