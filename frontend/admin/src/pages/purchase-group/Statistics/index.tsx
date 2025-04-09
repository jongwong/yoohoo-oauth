import React, { useMemo } from 'react';

import { useParams } from 'react-router-dom';
import { isNil, omitBy } from 'lodash';
import useRequest from '@/hooks/useRequest';
import http from '@/utils/http';
import { Descriptions, Tabs } from 'antd';
import { ProTable } from '@yoo/pro-component';

const Statistics: React.FC = () => {
	const params = useParams();
	const { groupId } = params as { groupId: string };
	const { data: orderData, loading: orderLoading } = useRequest(
		() => {
			const val = {
				page: 1,
				size: 400,
				group_id: groupId,
				// status: [
				// 	EOrderStatus.PendingDelivery,
				// 	EOrderStatus.Preparing,
				// 	EOrderStatus.InDelivery,
				// 	EOrderStatus.Completed,
				// ],
			};
			return http.get('/client/admin/order/with_refund', {
				params: omitBy(val, isNil),
			});
		},
		{
			refreshDeps: [groupId],
			ready: !!groupId,
		}
	);

	const { data: groupDetailData, loading } = useRequest(
		() => {
			return http.get(`/client/admin/group/${groupId}`, {
				params: {},
			});
		},
		{
			refreshDeps: [groupId],
			ready: !!groupId,
		}
	);

	function groupOrdersBySKU(data = []) {
		const groupedData = {};

		data?.forEach(order => {
			order?.items.forEach(item => {
				const key = item.sku_name ? `${item.product_name} - ${item.sku_name}` : item.product_name;

				if (!groupedData[key]) {
					groupedData[key] = {
						title: item.product_name + (item.sku_name ? '  ' + item.sku_name : ''),
						sku_name: item.sku_name || '',
						total_amount: 0,
						total_count: 0,
						orders: [],
					};
				}

				groupedData[key].total_amount += item.amount;
				groupedData[key].total_count += item.count;
				groupedData[key].orders.push(item);
			});
		});

		return Object.values(groupedData);
	}

	const getTitle = item => {
		return item.product_name + (item.sku_name ? '  ' + item.sku_name : '');
	};

	function groupOrdersByMobile(data = []) {
		const groupedData = {};

		data?.forEach((order: any) => {
			order?.items.forEach(item => {
				const key = item.consignee_mobile;

				if (!groupedData[key]) {
					groupedData[key] = {
						title: getTitle(item),
						sku_name: item.sku_name || '',
						total_amount: 0,
						total_count: 0,
						orders: [],
					};
				}

				const oldItemData = {
					...item,
					...order,
				};

				groupedData[key].consignee_mobile = order.consignee_mobile;
				groupedData[key].total_amount += item.amount;
				groupedData[key].total_count += item.count;
				groupedData[key].orders.push(oldItemData);
			});
		});

		return Object.values(groupedData);
	}

	const sortOrder = useMemo(() => {
		return (orderData || []).sort((a, b) => {
			return a.created_at - b.created_at;
		});
	}, [orderData]);
	const skuFormatData = useMemo(() => {
		return groupOrdersBySKU(sortOrder);
	}, [sortOrder]);
	const mobileFormatData = useMemo(() => {
		return groupOrdersByMobile(sortOrder);
	}, [sortOrder]);

	// 总数量
	const getTotalCount = data => {
		let totalCount = 0;
		data?.forEach(item => {
			totalCount += item.total_count;
		});
		return totalCount;
	};

	const renderInfo = () => {
		return (
			<div style={{ margin: '25px' }}>
				<Descriptions>
					<Descriptions.Item label={'团购名称'}>{groupDetailData?.name}</Descriptions.Item>
					<Descriptions.Item label={'配送地址'}>
						{groupDetailData?.distribution_point_name}
					</Descriptions.Item>
					<Descriptions.Item label={'总数量'}>{getTotalCount(skuFormatData)}</Descriptions.Item>
				</Descriptions>
			</div>
		);
	};

	return (
		<div
			className={'bg-white'}
			style={{
				height: '100vh',
				padding: '16px',
			}}>
			{renderInfo()}
			<Tabs
				type={'card'}
				items={[
					{
						key: 'mobile',
						label: '按收货人',
						children: (
							<ProTable
								dataSource={mobileFormatData}
								scroll={{ x: 'max-content' }}
								size={'small'}
								pagination={false}
								columns={[
									{
										title: '商品',
										dataIndex: 'title',
										render: (t, r) => {
											return (
												<div>
													{r?.orders?.map(item => (
														<div>
															{getTitle(item)}
															{'  *'} {item?.count}
														</div>
													))}
												</div>
											);
										},
									},
									{
										title: '数量',
										dataIndex: 'total_count',
									},
									{
										title: '手机尾号',
										dataIndex: 'consignee_mobile',
										render: t => {
											return String(t).slice(t.length - 4);
										},
									},
								]}
							/>
						),
					},
					{
						key: 'sku',
						label: '按sku',
						children: (
							<ProTable
								dataSource={skuFormatData}
								pagination={false}
								scroll={{ x: 'max-content' }}
								size={'small'}
								columns={[
									{
										title: '序号',
										dataIndex: '_index',
										render: (t, r, idx) => idx + 1,
									},
									{
										title: '商品',
										dataIndex: 'title',
										render: (t, r) => {
											return (
												<div>
													{r?.orders?.map(item => (
														<div>
															{getTitle(item)}
															{'  *'} {item?.count}
														</div>
													))}
												</div>
											);
										},
									},
									{
										title: '数量',
										dataIndex: 'total_count',
										width: '80px',
									},
								]}
							/>
						),
					},
				]}></Tabs>
		</div>
	);
};
export default Statistics;
