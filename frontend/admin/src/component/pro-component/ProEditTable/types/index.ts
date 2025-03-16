import { TableProps } from 'antd';

import { BaseTableProFieldFuncType } from '../../types';

export type ProEditTableColumnType<T = any> = BaseTableProFieldFuncType<T>;

export type ProEditTableProps = {
	name: string;
	columns: ProEditTableColumnType[];
	editable?: boolean;
	hideAddButton?: boolean;
	onInitRowData?: (newIdx: number) => Promise<any>;
} & Omit<TableProps<any>, 'dataSource' | 'columns'>;
