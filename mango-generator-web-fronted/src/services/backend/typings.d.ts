

declare namespace API {
  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseInteger = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };


  type BaseResponseLong = {
    code?: number;
    data?: string;
    message?: string;
  };



  type BaseResponsePageUser = {
    code?: number;
    data?: PageUser;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponsePageGeneratorVO = {
    code?: number;
    data?: PageGeneratorVO;
    message?: string;
  };




  type BaseResponsePageGenerator = {
    code?: number;
    data?: PageGenerator;
    message?: string;
  };


  type BaseResponsePostVO = {
    code?: number;
    data?: PostVO;
    message?: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseGenerator = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type BaseResponseGeneratorVO = {
    code?: number;
    data?: GeneratorVO;
    message?: string;
  };


  type checkParams = {
    timestamp: string;
    nonce: string;
    signature: string;
    echostr: string;
  };

  type DeleteRequest = {
    id?: string;
  };

  type getPostVOByIdParams = {
    id: string;
  };

  type getUserByIdParams = {
    id: string;
  };

  type getGeneratorByIdParams = {
    id: string;
  };

  type getUserVOByIdParams = {
    id: string;
  };

  type getGeneratorVOByIdParams={
    id:string
  }





  type LoginUserVO = {
    id?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PagePostVO = {
    records?: PostVO[];
    total?: string;
    size?: string;
    current?: string;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    countId?: string;
    maxLimit?: string;
    pages?: string;
  };

  type PageUser = {
    records?: User[];
    total?: string;
    size?: string;
    current?: string;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    countId?: string;
    maxLimit?: string;
    pages?: string;
  };

  type PageUserVO = {
    records?: UserVO[];
    total?: string;
    size?: string;
    current?: string;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    countId?: string;
    maxLimit?: string;
    pages?: string;
  };

  type PageGeneratorVO = {
    records?: GeneratorVO[];
    total?: string;
    size?: string;
    current?: string;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    countId?: string;
    maxLimit?: string;
    pages?: string;
  };

  type PageGenerator = {
    records?: Generator[];
    total?: string;
    size?: string;
    current?: string;
    orders?: OrderItem[];
    optimizeCountSql?: boolean;
    searchCount?: boolean;
    optimizeJoinOfCountSql?: boolean;
    countId?: string;
    maxLimit?: string;
    pages?: string;
  };



  type testDownloadFileUsingGETParams = {
    /** filepath */
    filepath?: string;
  };

  type uploadFileParams = {
    uploadFileRequest: UploadFileRequest;
  };

  type UploadFileRequest = {
    biz?: string;
  };


  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type User = {
    id?: string;
    userAccount?: string;
    userPassword?: string;
    unionId?: string;
    mpOpenId?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type Generator = {
    id?: string;
    name?: string;
    description?: string;
    backPackage?:string;
    version?:number;
    author?:string;
    tags?:string;
    picture?:string;
    fileConfig?:string;
    modelConfig?:string;
    distpath?:string;
    status?:number;
    userId?:string;
    isdelete?: number;

  };

  type UserAddRequest = {
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userRole?: string;
  };

  type userLoginByWxOpenParams = {
    code: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: string;
    pageSize?: string;
    sortField?: string;
    sortOrder?: string;
    id?: string;
    unionId?: string;
    mpOpenId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type GeneratorQueryRequest={
    current?: string;
    pageSize?: string;
    sortField?: string;
    sortOrder?: string;
    id?: string;
    name?: string;
    description?: string;
    tags?:string[];
  }


  type UserRegisterRequest = {
    userAccount?: string;
    userPassword?: string;
    checkPassword?: string;
  };

  type UserUpdateMyRequest = {
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
  };

  type GeneratorUpdateMyRequest = {
    name?: string;
    description?: string;
    backPackage?:string;
    version?:number;
    picture?:string;
    tags?:string[];
    fileConfig?:string;
    modelConfig?:string;
  };

  type GeneratorUpdateRequest = {
    name?: string;
    description?: string;
    backPackage?:string;
    version?:number;
    picture?:string;
    tags?:string[];
    fileConfig?:string;
    modelConfig?:string;
  };

  type  UserUpdateRequest = {
    id?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    id?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
  };


  type GeneratorVO = {
    id?: string;
    name?:string;
    description?: string;
    author?:string;
    tags?:string[];
    picture?:string;
    createTime?: string;
    user?:UserVO;
  };

  //todo 前端生成器的类型定义，需要根据实际情况修改
  type GeneratorAddRequest = {
    name?: string;
    description?: string;
    backPackage?:string;
    version?:number;
    picture?:string;
    tags?:string[];
    fileConfig?:string;
    modelConfig?:string;
    status?:number;
    dispatch?:string
  }

}
