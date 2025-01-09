import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';

import { Form, FormInstance, Table, TableProps, Tabs } from 'antd';
import { ColumnType } from 'antd/es/table/interface';
import { has, isBoolean, isNil, isNumber } from 'lodash';

import ProQueryForm, { ProQueryFormProps, QueryFormFieldType } from '../ProQueryForm';
import { BaseTableProFieldType } from '../types';
import useFormatFields from '../useFormatFields';

export type ProTableSearchFieldType<T = any> = QueryFormFieldType<T>;

export type ProTableColumnType<T = any> = Pick<
	BaseTableProFieldType<T>,
	'valueType' | 'valueEnum'
> &
	ColumnType<T>;

type FooterType<T = any> =
	| {
			initialValue?: number;
			name: string;
			items: {
				label: string;
				value: string;
			}[];
	  }
	| ((form: FormInstance<T>) => React.ReactNode);

export interface ProTableActionType<T = any> {
	reload: () => Promise<{
		total: number;
		data?: T[];
		success?: boolean;
	}>; // 暴露一个 reload 方法
}
export type ProTableProps<T = any, P = any> = {
	fields?: ProQueryFormProps<P>['fields'];
	columns: Array<ProTableColumnType<T>>;
	request?: (params: any) => Promise<{
		total: number;
		data?: T[];
	}>;
	operations?: (el: React.ReactNode[]) => React.ReactNode;
	extraOperation?: React.ReactNode;
	footer?: FooterType<T>;
	actionRef?: React.MutableRefObject<ProTableActionType | undefined>;
} & TableProps<T>;
const ProTable: React.FC<ProTableProps> = props => {
	const {
		actionRef,
		fields,
		loading: loadingProp,
		operations,
		extraOperation,
		footer,
		pagination,
		request,
		columns,
		...rest
	} = props;
	const { formatField } = useFormatFields();
	const [form] = Form.useForm();
	const formatColumns = (columns || []).map(
		it =>
			({
				...it,
				...formatField(it),
			} as ProTableColumnType)
	);

	const [dataSource, setDataSource] = useState([]);
	const [total, setTotal] = useState(0);
	const [loading, setLoading] = useState(false);
	const fetchData = async (mergeParams?: any) => {
		if (!request) {
			return;
		}
		const _request: any = request;
		const _pagination = isBoolean(pagination) ? {} : pagination;
		const params = form.getFieldsValue(true);
		setLoading(true);
		const res = await _request?.({
			page: _pagination?.current || _pagination?.defaultCurrent || 1,
			size: _pagination?.size || _pagination?.defaultPageSize || 20,
			...params,
			...mergeParams,
		}).finally?.(() => {
			setLoading(false);
		});

		setDataSource(res?.data || []);
		setTotal(res?.total || 0);
		return res;
	};
	useEffect(() => {
		fetchData();
	}, []);
	useImperativeHandle(actionRef, () => ({
		reload: () => {
			return fetchData({ page: 1 });
		},
	}));
	const initTabValueRef = useRef(
		isNil((footer as any)?.initialValue) ? -1 : (footer as any)?.initialValue
	);
	const renderFooter = () => {
		if (typeof footer === 'function') {
			return footer(form as any);
		}
		if (has(footer, 'items')) {
			const cfg = footer as any;
			return (
				<Form.Item shouldUpdate={true} noStyle>
					<Tabs
						defaultActiveKey={cfg?.initialValue}
						type={'card'}
						onChange={e => {
							const _val = Number(e);
							initTabValueRef.current = _val;
							form.setFields([{ name: cfg.name, value: isNumber(_val) ? _val : undefined }]);
							fetchData({ page: 1 });
						}}
						items={cfg?.items || []}
					/>
				</Form.Item>
			);
		}
	};

	useEffect(() => {
		const cfg = footer as any;

		if (cfg && has(cfg, 'initialValue')) {
			form.setFields([{ name: cfg.name, value: initTabValueRef.current }]);
		}
	}, [footer]);

	return (
		<div>
			{fields?.length ? (
				<Form form={form} className={'mb-24'}>
					<ProQueryForm
						fields={fields}
						form={form}
						onReset={() => {
							setTimeout(() => {
								const cfg = footer as any;
								if (cfg?.name) {
									form.setFields([
										{
											name: cfg.name,
											value: initTabValueRef.current,
										},
									]);
								}
							}, 100);
						}}
						extraOperation={extraOperation}
						operations={operations}
						onSearch={() => {
							fetchData({ page: 1 });
						}}
					/>
				</Form>
			) : null}

			{footer ? <div>{renderFooter()}</div> : null}
			<Table
				loading={loading || loadingProp}
				columns={formatColumns as any}
				size={'small'}
				rowKey={'id'}
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
