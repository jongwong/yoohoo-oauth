import React from "react";
import { ScrollView, View } from "@tarojs/components";
import styles from "./index.module.less";

interface Column {
  title: string;
  dataIndex: string;
  width?: string; // e.g. '200rpx', '20%', 'auto'
  render?: (val: any, record: any, index: number) => React.ReactNode;
}

interface TableProps {
  columns: Column[];
  dataSource: any[];
}

const Table: React.FC<TableProps> = ({ columns, dataSource }) => {
  // 构造 grid-template-columns
  const columnTemplate = columns.map((col) => col.width || "1fr").join(" ");

  return (
    <ScrollView scrollX className={styles.tableScroll}>
      <View className={styles.table}>
        {/* 表头 */}
        <View
          className={styles.gridRow}
          style={{ gridTemplateColumns: columnTemplate }}
        >
          {columns.map((col) => (
            <View
              className={`${styles.cell} ${styles.header}`}
              key={col.dataIndex}
            >
              {col.title}
            </View>
          ))}
        </View>

        {/* 数据行 */}
        {dataSource.map((row, rowIndex) => (
          <View
            className={styles.gridRow}
            style={{ gridTemplateColumns: columnTemplate }}
            key={rowIndex}
          >
            {columns.map((col) => (
              <View className={styles.cell} key={col.dataIndex}>
                {col.render
                  ? col.render(row[col.dataIndex], row, rowIndex)
                  : row[col.dataIndex]}
              </View>
            ))}
          </View>
        ))}
      </View>
    </ScrollView>
  );
};

export default Table;
