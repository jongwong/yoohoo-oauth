import React from 'react';
import { Button, Form, Table, TableProps } from 'antd';
import ProForm from '../ProForm';
import uuid from 'uuid';
import classNames from 'classnames';

import './index.less';

type ProEditTableProps = {
	name: string;
	columns: any[];
	editable?: boolean;
	onInitRowData?: (newIdx: number) => Promise<any>;
} & Omit<TableProps<any>, 'dataSource' | 'columns'>;
const ProEditTable: React.FC<ProEditTableProps> = props => {
	const { name, editable = false, onInitRowData, columns, ...rest } = props;

	const form = Form.useFormInstance();

	const formatColumns = columns.map(column => {
		return {
			...column,
			render: (text: any, record: any, index: number) => {
				return <ProForm.Item {...column} name={[index, column.dataIndex]} allEditable={editable} />;
			},
		};
	});

	const renderFooter = (add: any, newIdx: number) => {
		return editable ? (
			<Button
				type={'dashed'}
				block
				onClick={async () => {
					const data = onInitRowData?.(newIdx);
					add({ __uuid: uuid(), ...data });
				}}>
				{'+ 添加'}
			</Button>
		) : null;
	};
	return (
		<div
			className={classNames(
				'yoo-pro-edit-table',
				editable && 'yoo-pro-edit-table-editable',
				'brick-pro-edit-table-hint-single'
				// mode === 'cell' && 'brick-pro-edit-table-mode-cell',
				// hintMode === 'multiple'
				// 	? 'brick-pro-edit-table-hint-multiple'
				// 	: 'brick-pro-edit-table-hint-single'
			)}>
			<Form.List name={name}>
				{(fields, { add, remove }) => {
					const dataSource = fields.map((field, index) => {
						return form.getFieldValue(name)[index] || {};
					});
					return (
						<Table
							size={'small'}
							rowKey={(e: any) => e?.id || e?.__uuid}
							pagination={false}
							columns={formatColumns}
							dataSource={dataSource}
							footer={() => renderFooter(add, dataSource.length)}
							{...rest}
						/>
					);
				}}
			</Form.List>
		</div>
	);
};
export default ProEditTable;
