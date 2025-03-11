import Layout from "@/component/Layout";

import React, { useState } from "react";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import { Form, FormItem, Image } from "@antmjs/vantui";
import { Input, Textarea } from "@tarojs/components";
import ProxyWrapped from "@/component/ProxyWrapped";
import DistributionPointSelect from "@/pages/admin/group/detail/DistributionPointSelect";
import TimePicker from "@/pages/admin/group/detail/TimePicker";
import dayjs from "dayjs";
import { generateFileUrl } from "@/utils/file";
import OssUpload from "@/component/OssUpload";

const Index: React.FC = () => {
  const router = useRouter();
  const form = Form.useForm();
  const [readonly, setReadonly] = useState(false);
  const { runAsync: fetchGroupData, data: groupDetailData } = useRequest(
    () => {
      return request.get(`/client/admin/group/${router.params?.id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.id],
      ready: !!router.params?.id,
      onSuccess: (res) => {
        const val = res?.data || {};
        val.img_url = val.img_url
          ? [
              {
                url: val?.img_url,
                name: val?.img_url,
              },
            ]
          : undefined;

        form.setFields(val);
        form.setFieldsValue("img_url", val.img_url);
        return res?.data;
      },
    }
  );
  const formatTime = (e?: number) => {
    return e ? dayjs(e).format("YYYY-MM-DD HH:mm:ss") : "--";
  };
  return (
    <Layout
      edge={"none"}
      // footer={
      //   <View>
      //     <Button
      //       type="primary"
      //       block
      //       size={"small"}
      //       onClick={async () => {
      //         form.submit((errs, val) => {});
      //       }}
      //     >
      //       申请退款
      //     </Button>
      //   </View>
      // }
    >
      <view style={{ padding: "16px", backgroundColor: "#fff" }}>
        <Form form={form} className={"w-1-1"}>
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
              <ProxyWrapped>{(cfg) => cfg?.value}</ProxyWrapped>
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
                {(cfg) => groupDetailData?.distribution_point_name}
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
              <ProxyWrapped>{(cfg) => cfg?.value}</ProxyWrapped>
            ) : (
              <Textarea />
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
              <ProxyWrapped>{(cfg) => cfg?.value}</ProxyWrapped>
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
              <ProxyWrapped>{(cfg) => formatTime(cfg?.value)}</ProxyWrapped>
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
              <ProxyWrapped>{(cfg) => formatTime(cfg?.value)}</ProxyWrapped>
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
              <ProxyWrapped>{(cfg) => formatTime(cfg?.value)}</ProxyWrapped>
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
              <ProxyWrapped>{(cfg) => formatTime(cfg?.value)}</ProxyWrapped>
            ) : (
              <TimePicker />
            )}
          </FormItem>
          <FormItem
            label="团购图片"
            name="img_url"
            required
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
                    src={generateFileUrl(groupDetailData?.img_url[0]?.url)}
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
        </Form>
      </view>
    </Layout>
  );
};
export default Index;
