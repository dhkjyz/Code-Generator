// @ts-ignore

import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /file/upload */
export async function uploadFile(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadFileParams,
  body: {},
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString>('/file/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
      uploadFileRequest: undefined,
      ...params['uploadFileRequest'],
    },
    data: body,
    ...(options || {}),
  });
}
/** testDownloadFile GET /api/file/test/download/ */
export async function testDownloadFileUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.testDownloadFileUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<any>('/file/test/download/', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function testUploadFileUsingPost(
  body: NonNullable<unknown>,
  file?: File,
  options?: { [key: string]: any },
) {
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.BaseResponseString_>('/file/test/upload', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}
