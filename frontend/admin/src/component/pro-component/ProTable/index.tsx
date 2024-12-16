import React, { ReactNode, useEffect, useState } from 'react';
import ProQueryForm, { ProQueryFormProps } from '@/component/pro-component/ProQueryForm';
import { Form, Table, TableProps } from 'antd';
import { BaseProFieldType } from '@/component/pro-component/types';
import { formatProField } from '@/component/pro-component/utils/render';
import { ColumnType } from 'antd/es/table/interface';
import { isBoolean, isNil } from 'lodash';

export type ProTableColumnType<T = any> = Omit<
	BaseProFieldType<T>,
	'title' | 'label' | 'name' | 'render'
> & {
	title: ReactNode;
	render?: (text: any, record: T, index: number) => ReactNode;
} & ColumnType<T>;

export type ProTableProps<T = any, P = any> = {
	fields: ProQueryFormProps<P>['fields'];
	columns: Array<ProTableColumnType<T>>;
	request?: (params: any) => Promise<{
		total: number;
		data?: T[];
	}>;
} & TableProps<T>;
const ProTable: React.FC<ProTableProps> = props => {
	const { fields, pagination, request, columns, ...rest } = props;
	const [form] = Form.useForm();
	const formatColumns = (columns || []).map(it => formatProField(it, true));

	const [dataSource, setDataSource] = useState([]);
	const [total, setTotal] = useState(0);
	const [loading, setLoading] = useState(false);
	const fetchData = (mergeParams?: any) => {
		if (!request) {
			return;
		}
		const _request: any = request;
		const _pagination = isBoolean(pagination) ? {} : pagination;
		const params = form.getFieldsValue(true);
		setLoading(true);
		_request?.({
			page: _pagination?.current || _pagination?.defaultCurrent || 1,
			size: _pagination?.size || _pagination?.defaultPageSize || 20,
			...params,
			...mergeParams,
		})
			.then((re: any) => {
				setDataSource(re?.data || []);
				setTotal(re?.total || 0);
			})
			.finally?.(() => {
				setLoading(false);
			});
	};
	useEffect(() => {
		fetchData();
	}, []);
	return (
		<div>
			<Form form={form}>
				<ProQueryForm
					fields={fields}
					form={form}
					onSearch={() => {
						fetchData({ page: 1 });
					}}
				/>
			</Form>

			<Table
				loading={loading}
				columns={formatColumns as any}
				size={'small'}
				dataSource={dataSource}
				pagination={
					pagination || isNil(pagination)
						? {
								defaultCurrent: 1,
								defaultPageSize: 20,
								total: total,
								showTotal: e => `共${e}条`,
								...pagination,
								onChange: (p, s) => {
									if (!isBoolean(pagination)) {
										pagination?.onChange?.(p, s);
									}
									fetchData({ page: p, size: s });
								},
						  }
						: pagination
				}
				{...rest}
			/>
		</div>
	);
};
export default ProTable;
