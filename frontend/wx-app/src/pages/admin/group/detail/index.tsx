import Layout from "@/component/Layout";

import React, { useRef, useState } from "react";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import {
  Button,
  Cell,
  Divider,
  Form,
  FormItem,
  Image,
  ImageViewer,
  Toast,
} from "@antmjs/vantui";
import { Input, Text, Textarea, View } from "@tarojs/components";
import ProxyWrapped from "@/component/ProxyWrapped";
import DistributionPointSelect from "@/pages/admin/group/detail/DistributionPointSelect";
import TimePicker from "@/pages/admin/group/detail/TimePicker";
import dayjs from "dayjs";
import { generateFileUrl } from "@/utils/file";
import OssUpload from "@/component/OssUpload";
import ProductSelect from "@/pages/admin/group/detail/ProductSelect";
import { useUpdate } from "ahooks";
import { divide, multiply } from "@/utils/number";
import { cloneDeep, isNumber, uniqueId } from "lodash-es";
import {
  createPurchaseGroup,
  updatePurchaseGroup,
} from "@/pages/admin/group/detail/service";
import { EMPTY_TEXT } from "@/constant";
import InputNumber from "@/component/InputNumber";

const Index: React.FC = () => {
  const router = useRouter();
  const form = Form.useForm();

  const [detailData, setDetailData] = useState({});
  const [saveLoading, setSaveLoading] = useState(false);
  const groupId = router.params?.id;
  const [readonly, setReadonly] = useState(false);

  const productListRef = useRef([]);
  const [productList, _setProductList] = useState([]);
  const [formUidKey, setFormUidKey] = useState("");
  const setProductList = (e) => {
    productListRef.current = e;
    _setProductList(e);
  };
  const getFormatData = (data) => {
    const val = cloneDeep(data || {});
    val.img_url = val.img_url
      ? [
          {
            url: val?.img_url,
            name: val?.img_url,
          },
        ]
      : undefined;
    return val;
  };
  const historyDataRef = useRef();
  const forceUpdate = useUpdate();
  const {
    runAsync: fetchGroupData,
    data: groupDetailData,
    loading,
  } = useRequest(
    () => {
      return request.get(`/client/admin/group/${router.params?.id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.id],
      ready: !!router.params?.id,
      onSuccess: (res) => {
        const data = getFormatData(res?.data);

        setDetailData(data);
        setProductList(data.products);
        setFormUidKey(uniqueId());
        return res?.data;
      },
    }
  );
  const formatTime = (e?: number) => {
    return e ? dayjs(e).format("YYYY-MM-DD HH:mm:ss") : "--";
  };
  const onRemove = (idx: number) => {
    const old = productList || [];

    const newList = old.filter((item, index) => index !== idx);
    setProductList(newList);
    forceUpdate();
  };

  const formatAmount = (e) => {
    return isNumber(e) ? String(divide(e, 100)) : undefined;
  };

  const renderProductItem = (item, idx) => {
    return (
      <View
        style={{
          marginBottom: "12px",
          borderRadius: "8px",
          backgroundColor: "#f8f8f8",
          position: "relative",
        }}
      >
        {/* 商品标题 */}
        <Cell
          title={`${item.product_name}`}
          titleStyle={{
            fontSize: "18px",
          }}
          isLink={false}
          renderExtra={
            <View style={{ textAlign: "right", marginTop: "10px" }}>
              <Button
                type="primary"
                size="small"
                onClick={() => onRemove(idx)}
                plain
              >
                删除
              </Button>
            </View>
          }
        />

        <FormItem
          label="商品图片"
          name={["products", idx, "thumbnail_image"]}
          trigger="onInput"
          valueFormat={(e) => e.detail.value}
        >
          <ProxyWrapped>
            {(cfg) => (
              <Image
                src={generateFileUrl(item?.thumbnail_image)}
                width={60}
                height={60}
                onClick={() =>
                  ImageViewer.show({
                    list: [generateFileUrl(item?.thumbnail_image)],
                    currentIndex: 0,
                  })
                }
              />
            )}
          </ProxyWrapped>
        </FormItem>

        <FormItem
          label="售卖价格"
          name={["products", idx, "price"]}
          trigger="onInput"
          valueFormat={(e) => e.detail.value}
        >
          <ProxyWrapped>
            {(cfg) => (
              <Text>
                {isNumber(item?.price) ? formatAmount(item?.price) : EMPTY_TEXT}
              </Text>
            )}
          </ProxyWrapped>
        </FormItem>
        <FormItem
          label="商品降价"
          name={["products", idx, "amount_offset"]}
          trigger="onChange"
        >
          {readonly ? (
            <ProxyWrapped>
              {(cfg) => (
                <Text>
                  {isNumber(item?.amount_offset)
                    ? formatAmount(item?.amount_offset)
                    : EMPTY_TEXT}
                </Text>
              )}
            </ProxyWrapped>
          ) : (
            <ProxyWrapped>
              {(cfg) => (
                <InputNumber
                  precision={2}
                  placeholder={"请输入降价金额(增量)"}
                  value={formatAmount(item.amount_offset)}
                  onChange={(e) => {
                    const val = e;
                    item.amount_offset = isNumber(val)
                      ? multiply(val, 100)
                      : undefined;

                    const old = productListRef.current;
                    old[idx] = item;
                    setProductList(cloneDeep(old));
                  }}
                />
              )}
            </ProxyWrapped>
          )}
        </FormItem>

        <FormItem
          label="最大库存"
          name={["products", idx, "max_stock"]}
          trigger="onInput"
          valueFormat={(e) => e.detail.value}
        >
          {readonly ? (
            <ProxyWrapped>
              {(cfg) => (
                <Text>
                  {isNumber(item?.price) ? item?.max_stock : EMPTY_TEXT}
                </Text>
              )}
            </ProxyWrapped>
          ) : (
            <ProxyWrapped>
              {(cfg) => (
                <Input
                  value={item.max_stock}
                  onInput={(e) => {
                    const val = Number(e.detail.value);

                    item.max_stock = val;

                    const old = productListRef.current;
                    old[idx] = item;
                    setProductList(cloneDeep(old));
                  }}
                  type={"number"}
                  placeholder={"请输入数量"}
                />
              )}
            </ProxyWrapped>
          )}
        </FormItem>

        {/* 分隔线（用于多个商品之间的间隔） */}
        <Divider />
      </View>
    );
  };
  return (
    <Layout
      edge={"none"}
      loading={loading || saveLoading}
      footer={
        <View style={{ padding: "10px" }}>
          {readonly ? (
            <>
              <Button
                type="primary"
                block
                onClick={async () => {
                  historyDataRef.current = detailData;
                  setProductList(detailData.products);
                  form.setFields(detailData);

                  setReadonly(false);
                }}
              >
                编辑
              </Button>
            </>
          ) : (
            <View style={{ display: "flex", gap: "10px" }}>
              {/*<Button*/}
              {/*  type="primary"*/}
              {/*  plain*/}
              {/*  hairline*/}
              {/*  block*/}
              {/*  onClick={async () => {*/}
              {/*    const val = historyDataRef.current;*/}
              {/*    form.resetFields();*/}
              {/*    form.setFields(val);*/}
              {/*    historyDataRef.current = undefined;*/}
              {/*    setReadonly(true);*/}
              {/*    setFormUidKey(uniqueId());*/}
              {/*  }}*/}
              {/*>*/}
              {/*  取消*/}
              {/*</Button>*/}
              <Button
                type="primary"
                block
                onClick={async () => {
                  form.validateFields(async () => {
                    const val = form.getFieldsValue();
                    val.products = productList;
                    val.img_url = val?.img_url?.[0]?.url;
                    const fn = groupId
                      ? updatePurchaseGroup(groupId, val)
                      : createPurchaseGroup(val);

                    const res = await fn.finally(() => {
                      setSaveLoading(false);
                    });
                    if (res.success) {
                      Toast.success({
                        children: "保存成功",
                        duration: 2000,
                      });
                    }
                  });
                }}
              >
                保存
              </Button>
            </View>
          )}
        </View>
      }
    >
      <view style={{ padding: "16px", backgroundColor: "#fff" }}>
        <Form
          form={form}
          key={formUidKey}
          initialValues={detailData}
          className={"w-1-1"}
        >
          <FormItem
            label="团购名称"
            name="name"
            required
            trigger="onInput"
            validateTrigger="onBlur"
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => e.detail.value}
          >
            {readonly ? (
              <ProxyWrapped>{(cfg) => cfg?.value || EMPTY_TEXT}</ProxyWrapped>
            ) : (
              <Input placeholder="请输入团购名称" className={"w-1-1"} />
            )}
          </FormItem>

          <FormItem
            label="配送地址"
            name="distribution_point_id"
            required
            trigger="onInput"
            validateTrigger="onBlur"
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => e.detail.value}
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) =>
                  groupDetailData?.distribution_point_name || EMPTY_TEXT
                }
              </ProxyWrapped>
            ) : (
              <DistributionPointSelect />
            )}
          </FormItem>

          <FormItem
            label="团购描述"
            name="description"
            required
            trigger="onInput"
            validateTrigger="onBlur"
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => e.detail.value}
          >
            {readonly ? (
              <ProxyWrapped>{(cfg) => cfg?.value || EMPTY_TEXT}</ProxyWrapped>
            ) : (
              <Textarea maxlength={200} autoHeight />
            )}
          </FormItem>

          <FormItem
            label="最小成团人数"
            name="group_required_count"
            required
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => e.detail.value}
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (
                  <Text>{isNumber(cfg?.value) ? cfg?.value : EMPTY_TEXT}</Text>
                )}
              </ProxyWrapped>
            ) : (
              <Input type={"number"} />
            )}
          </FormItem>

          <FormItem
            label="开始时间"
            name="time_start"
            required
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) =>
              dayjs(e.detail.value).startOf("minute").valueOf()
            }
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (cfg?.value ? formatTime(cfg?.value) : EMPTY_TEXT)}
              </ProxyWrapped>
            ) : (
              <TimePicker />
            )}
          </FormItem>
          <FormItem
            label="结束时间"
            name="time_end"
            required
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => dayjs(e.detail.value).endOf("minute").valueOf()}
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (cfg?.value ? formatTime(cfg?.value) : EMPTY_TEXT)}
              </ProxyWrapped>
            ) : (
              <TimePicker />
            )}
          </FormItem>
          <FormItem
            label="配送开始时间"
            name="time_delivery_start"
            required
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) =>
              dayjs(e.detail.value).startOf("minute").valueOf()
            }
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (cfg?.value ? formatTime(cfg?.value) : EMPTY_TEXT)}
              </ProxyWrapped>
            ) : (
              <TimePicker />
            )}
          </FormItem>
          <FormItem
            label="配送结束时间"
            name="time_delivery_end"
            required
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
            valueFormat={(e) => dayjs(e.detail.value).endOf("minute").valueOf()}
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (cfg?.value ? formatTime(cfg?.value) : EMPTY_TEXT)}
              </ProxyWrapped>
            ) : (
              <TimePicker />
            )}
          </FormItem>
          <FormItem
            label="团购封面图片"
            name="img_url"
            required
            mutiLevel
            valueFormat={(e) => {
              return e;
            }}
            // taro的input的onInput事件返回对应表单的最终值为e.detail.value
          >
            {readonly ? (
              <ProxyWrapped>
                {(cfg) => (
                  <Image
                    width={100}
                    height={100}
                    src={generateFileUrl(detailData?.img_url?.[0]?.url)}
                    onClick={() =>
                      ImageViewer.show({
                        list: [generateFileUrl(detailData?.img_url?.[0]?.url)],
                        currentIndex: 0,
                      })
                    }
                  />
                )}
              </ProxyWrapped>
            ) : (
              <ProxyWrapped>
                {(cfg) => {
                  return <OssUpload {...cfg} />;
                }}
              </ProxyWrapped>
            )}
          </FormItem>
          {productListRef.current?.map((item, index) =>
            renderProductItem(item, index)
          )}
          <FormItem
            name="products"
            mutiLevel
            label="复杂数据"
            style={{ display: "none" }}
          />

          {!readonly ? (
            <View>
              <ProductSelect
                onChange={(item) => {
                  const old: any[] = productList || [];
                  setProductList([
                    ...old,
                    {
                      product_id: item.id,
                      product_name: item.name,
                      product_code: item.code,
                      price: item.price,
                      thumbnail_image: item?.thumbnail_image?.url,
                    } as any,
                  ]);
                  forceUpdate();
                }}
                renderInput={({ open }) => (
                  <Button
                    type="primary"
                    hairline
                    plain
                    icon={"add"}
                    block
                    onClick={() => open()}
                    style={{ marginBottom: "10px" }}
                  >
                    添加商品
                  </Button>
                )}
              />
            </View>
          ) : null}
        </Form>
      </view>
    </Layout>
  );
};
export default Index;
