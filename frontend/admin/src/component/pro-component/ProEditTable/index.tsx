import React, { useRef } from 'react';
import { Button, Form, Table } from 'antd';
import uuid from 'uuid';
import classNames from 'classnames';

import { ProEditTableProps } from './types';
import { getKeyList } from '../utils/not-export';

import ProField from '../ProField';

import './index.less';

const ProEditTable: React.FC<ProEditTableProps> = props => {
	const { name, editable = false, onInitRowData, columns, ...rest } = props;

	const form = Form.useFormInstance();
	const operationsRef = useRef<any>();
	const formatColumns = columns
		.map(column => {
			return {
				...column,
				render: (t, r, idx) => {
					const li = getKeyList(column?.dataIndex);
					const itemName = [idx, ...li];
					const fieldName = [...getKeyList(name), ...itemName];
					return (
						<ProField
							label={undefined}
							{...column}
							name={itemName}
							_isTable
							allEditable={editable}
							getRecord={() => r}
							getArgs={e => {
								const operations = {
									remove: () => {
										operationsRef.current?.remove?.(idx);
									},
								};
								if (e) {
									return [t, r, idx, { index: idx, fieldName: fieldName, operations }];
								} else {
									return [t, r, idx, { index: idx, fieldName: fieldName, operations }];
								}
							}}
						/>
					);
				},
			};
		})
		.filter(e => e.visible !== false);

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
				{(fields, operations) => {
					operationsRef.current = operations;
					const { add, remove } = operations;
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
