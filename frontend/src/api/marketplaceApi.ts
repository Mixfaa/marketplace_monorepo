import axios, { AxiosHeaders, type AxiosInstance, type AxiosStatic } from "axios"
import qs from "qs"
import { get } from "svelte/store"
import { persisted } from 'svelte-persisted-store'

export const MARKET_PLACE_ENDPOINT = "http://localhost:8080/v2"

export function is_successful(status: number) {
    return status >= 200 && status < 300
}

export function makeMarketplaceClient(): AxiosInstance {
    return axios.create({
        baseURL: MARKET_PLACE_ENDPOINT,
    })
}

export const credentials = persisted('credentials', {
    'username': '',
    'password': '',
    'role': ''
})

export const DEFAULT_PAGE_SIZE = 15

export interface IIndexedProp {
    prop: string,
    values: string[]
}

export interface IPage<T> {
    content: T[],
    totalPages: number,
    totalElements: number
}

export interface IRealizedProductDto {
    caption: string,
    description: string,
    productId: string,
    quantity: number,
    price: number
}

export interface IOrderDto {
    id: string,
    products: [IRealizedProductDto],
    ownerId: string,
    status: string,
    shippingAddress: string,
    timestamp: Date
}

export interface IOrderBuilder {
    id: string,
    ownerId: string,
    products: any // string to number map
}

export interface IQueryAndSort {
    query: any,
    sort: any | string
}

export interface IStoredFileDto {
    id: string,
    name: string,
    owner: string
}

export interface ICategoryDto {
    id: string,
    name: string,
    subcategoriesIds: string[],
    requiredProps: string[]
}

export interface ICommentDto {
    id: string,
    ownerId: string,
    productId: string,
    content: string,
    rate: number,
    timestamp: Date
}

export interface IAccountRegisterDto {
    username: string,
    firstname: string,
    lastname: string,
    password: string,
    mailCode: string,
    role?: string,
    adminSecret?: string
}

export interface IProductDto {
    id: string,
    caption: string,
    categories: ICategoryDto[],
    characteristics: object,
    description: string,
    price: number,
    actualPrice: number,
    rate: number,
    ordersCount: number,
    availableQuantity: number,
    images: string[]
}

export interface IProductRegisterDto {
    caption: string,
    categories: string[],
    characteristics: Map<string, string>,
    description: string,
    price: number,
    availableQuantity: number,
    images: string[]
}

export interface ICategoryRegisterDto {
    name: string,
    parentCategory?: string,
    requiredProps: string[]
}

export class PublicMarketplaceApi {
    marketplaceClient: AxiosInstance

    constructor(client: AxiosInstance) {
        this.marketplaceClient = client
    }

    static make(): PublicMarketplaceApi {
        return new PublicMarketplaceApi(makeMarketplaceClient())
    }

    register(request: IAccountRegisterDto) {
        return this.marketplaceClient.post(`/account/register`, request)
    }

    sendEmailCode(email: string) {
        return this.marketplaceClient.post(`/account/send_email_code`, qs.stringify({
            "email": email
        }))
    }

    findUsers(query: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/account/users`, {
            params: {
                "query": query,
                "page": page,
                "pageSize": pageSize
            }
        })
    }
}

export function isAuthenticated() {
    return get(credentials).username != '' && get(credentials).password != ''
}

export async function authenticate(username: string, password: string) {
    const privateApi = new PrivateMarketplaceApi(username, password)
    const getMeResult = await privateApi.getMe()
    if (!is_successful(getMeResult.status))
        return undefined

    credentials.set({
        'username': username,
        'password': password,
        'role': getMeResult.data.role
    })

    return privateApi
}

export function logout() {
    credentials.set({
        username: "",
        password: "",
        role: ""
    })
}

export class PrivateMarketplaceApi {
    authHeaders: AxiosHeaders
    authenticatedClient: AxiosInstance
    marketplaceClient: AxiosInstance

    static fromLocalStorage() {
        const username = get(credentials).username
        const password = get(credentials).password

        return new PrivateMarketplaceApi(username, password)
    }

    constructor(username: string, password: string) {
        this.authHeaders = new AxiosHeaders()
        this.authHeaders.set('Authorization', 'Basic ' + btoa(username + ":" + password))
        this.authHeaders.set('Access-Control-Allow-Origin', '*')
        this.authenticatedClient = axios.create({
            baseURL: MARKET_PLACE_ENDPOINT,
            headers: this.authHeaders
        })

        this.marketplaceClient = makeMarketplaceClient()
    }

    uploadFile(form: FormData) {
        return this.authenticatedClient.post(`/file-storage/file`, form, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    }

    uploadFileByURL(filename: string, url: string) {
        return this.authenticatedClient.post(`/file-storage/file_by_url`, qs.stringify({
            "name": filename,
            "url": url
        }))
    }

    fileInfo(fileId: string) {
        return this.marketplaceClient.get(`/file-storage/${fileId}/info`)
    }

    fileBytes(fileId: string) {
        return this.marketplaceClient.get(`/file-storage/${fileId}/bytes`)
    }

    getFile(fileId: string) {
        return this.marketplaceClient.get(`/file-storage/${fileId}`)
    }

    deleteFile(fileId: string) {
        return this.authenticatedClient.delete(`/file-storage/${fileId}`)
    }

    getMe() {
        return this.authenticatedClient.get(`/account/me`)
    }

    addShippingAddress(shippingAddress: string) {
        return this.authenticatedClient.post(`/account/shipping_address`, qs.stringify({
            "shippingAddress": shippingAddress
        }))
    }

    removeShippingAddress(shippingAddress: string) {
        return this.authenticatedClient.delete(`/account/shipping_address`, {
            params: {
                "shippingAddress": shippingAddress
            }
        })
    }

    /**
     * Categories
     */

    registerCategory(registerRequest: any) {
        return this.authenticatedClient.post(`/marketplace/categories/register`, registerRequest)
    }

    findCategories(query: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/categories/find`, {
            params: {
                "query": query,
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    findCategory(id: string) {
        return this.marketplaceClient.get(`/marketplace/categories/${id}`)
    }

    listCategory(page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/categories`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    countCategories() {
        return this.marketplaceClient.get(`/marketplace/categories/count`)
    }

    /*
    * Products
    */
    updateProduct(productId: string, registerRequest: IProductRegisterDto) {
        const request = {
            caption: registerRequest.caption,
            categories: registerRequest.categories,
            characteristics: Object.fromEntries(registerRequest.characteristics),
            description: registerRequest.description,
            availableQuantity: registerRequest.availableQuantity,
            price: registerRequest.price,
            images: registerRequest.images
        }
        return this.authenticatedClient.post(`/marketplace/products/${productId}/update`, request)
    }

    registerProduct(registerRequest: IProductRegisterDto) {
        const request = {
            caption: registerRequest.caption,
            categories: registerRequest.categories,
            characteristics: Object.fromEntries(registerRequest.characteristics),
            description: registerRequest.description,
            availableQuantity: registerRequest.availableQuantity,
            price: registerRequest.price,
            images: registerRequest.images
        }
        return this.authenticatedClient.post(`/marketplace/products/register`, request)
    }

    deleteProduct(productId: string) {
        return this.authenticatedClient.delete(`/marketplace/products/${productId}`)
    }

    getProduct(productId: string) {
        return this.marketplaceClient.get(`/marketplace/products/${productId}`)
    }

    findProducts(query: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/products/find`, {
            params: {
                "query": query,
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    findProductsV2(query: string, sort: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/products/findV2`, {
            params: {
                "query": btoa(query),
                "sort": btoa(sort),
                "page": page,
                "pageSize": pageSize
            }
        })
    }


    findProductsV3(query: string, precompsort: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/products/findV3`, {
            params: {
                "query": btoa(query),
                "sort": precompsort,
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    countProducts() {
        return this.marketplaceClient.get(`/marketplace/products/count`)
    }

    addProductImage(productId: string, imageLink: string) {
        return this.authenticatedClient.post(`/marketplace/products/${productId}/image`, qs.stringify({
            "image": imageLink
        }))
    }

    deleteProductImage(productId: string, imageLink: string) {
        return this.authenticatedClient.delete(`/marketplace/products/${productId}/image`, {
            params: {
                "image": imageLink
            }
        })
    }

    /*
    * Discounts
    */
    registerDiscount(registerRequest: any) {
        return this.authenticatedClient.post(`/marketplace/discounts/register`, registerRequest)
    }

    deleteDiscount(discountId: string) {
        return this.authenticatedClient.delete(`/marketplace/discounts/${discountId}`)
    }

    listDiscounts(page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/discounts`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    findDiscounts(query: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/discounts/${query}`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    /*
    * Orders
    */

    getCurrentOrder() {
        return this.authenticatedClient.get(`/marketplace/orders/current`)
    }

    addProductToOrder(productId: string, quantity: number) {
        return this.authenticatedClient.post(`/marketplace/orders/product/${productId}`, qs.stringify({
            "quantity": quantity
        }))
    }

    removeProductFromOrder(productId: string) {
        return this.authenticatedClient.delete(`/marketplace/orders/product/${productId}`)
    }

    makeOrder(shippingAddress: string, promocode: string | undefined) {
        return this.authenticatedClient.post(`/marketplace/orders/make`, qs.stringify({
            "shippingAddress": shippingAddress,
            "promoCode": promocode
        }))
    }

    cancelOrder(orderId: string) {
        return this.authenticatedClient.post(`/marketplace/orders/cancel`, qs.stringify({
            "orderId": orderId
        }))
    }

    changeOrderStatus(orderId: string, newStatus: string) {
        return this.authenticatedClient.post(`/marketplace/orders/${orderId}/change_status`, qs.stringify({
            "newStatus": newStatus
        }))
    }

    listMyOrders(page: number, pageSize: number) {
        return this.authenticatedClient.get(`/marketplace/orders/list_my`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    countMyOrders() {
        return this.authenticatedClient.get(`/marketplace/orders/count_my`)
    }

    /*
    * Favlists
    */
    createFavoriteList(registerRequest: any) {
        return this.authenticatedClient.post(`/marketplace/favlists/create`, registerRequest)
    }

    deleteFavoriteList(listId: string) {
        return this.authenticatedClient.delete(`/marketplace/favlists/${listId}`)
    }

    changeFavoriteListVisibility(listId: string, isPublic: boolean) {
        return this.authenticatedClient.post(`/marketplace/favlists/${listId}/change_visibility`, qs.stringify({
            "isPublic": isPublic
        }))
    }

    addProductToFavoriteList(listId: string, productId: string) {
        return this.authenticatedClient.post(`/marketplace/favlists/${listId}/product/${productId}`)
    }

    removeProductFromFavoriteList(listId: string, productId: string) {
        return this.authenticatedClient.delete(`/marketplace/favlists/${listId}/product/${productId}`)
    }

    getMyFavoriteLists() {
        return this.authenticatedClient.get(`/marketplace/favlists/my`)
    }

    getPublicFavoriteList(listId: string) {
        return this.marketplaceClient.get(`/marketplace/favlists/${listId}/get_public`)
    }

    getUserPublicFavoriteLists(accountId: string) {
        return this.marketplaceClient.get(`/marketplace/favlists/from_account/${accountId}`)
    }

    /*
    * Comments
    */

    registerComment(registerRequest: any) {
        return this.authenticatedClient.post(`/marketplace/comments/register`, registerRequest)
    }

    deleteComment(commentId: string) {
        return this.authenticatedClient.delete(`/marketplace/comments/${commentId}`)
    }

    listComments(productId: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/comments/from_product/${productId}`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    listMyComments(page: number, pageSize: number) {
        return this.authenticatedClient.get(`/marketplace/comments/my`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    listIndexesFor(categoryId: string, prop: string) {
        return this.marketplaceClient.get(`/marketplace/indexer/${categoryId}/${prop}`)
    }

    async listIndexesForWrapped(categoryId: string, prop: string) {
        return (await this.listIndexesFor(categoryId, prop)).data as string[]
    }

    listIndexes(categoryId: string, page: number, pageSize: number) {
        return this.marketplaceClient.get(`/marketplace/indexer/${categoryId}`, {
            params: {
                "page": page,
                "pageSize": pageSize
            }
        })
    }

    async listIndexesWrapped(categoryId: string, page: number, pageSize: number) {
        return (await this.listIndexes(categoryId, page,pageSize)).data as IPage<IIndexedProp>
    }

}

export async function findCategory(privateApi: PrivateMarketplaceApi, id: string) {
    return ((await privateApi.findCategory(id)).data) as ICategoryDto
}

export async function loadCategories(privateApi: PrivateMarketplaceApi, page: number = 0, pageSize: number = DEFAULT_PAGE_SIZE) {
    return (await privateApi.listCategory(page, pageSize)).data as IPage<ICategoryDto>
}

export async function loadUser(privateApi: PrivateMarketplaceApi) {
    return (await privateApi.getMe()).data;
}

export async function loadOrderBuilder(privateApi: PrivateMarketplaceApi) {
    return (await privateApi.getCurrentOrder()).data as IOrderBuilder;
}

export async function loadProduct(privateApi: PrivateMarketplaceApi, productId: string) {
    return (await privateApi.getProduct(productId)).data as IProductDto;
}

export async function loadProducts(privateApi: PrivateMarketplaceApi, query: string, page: number = 0, pageSize: number = DEFAULT_PAGE_SIZE) {
    return (await privateApi.findProducts(query, page, pageSize)).data as IPage<IProductDto>;
}

export async function findProducts(privateApi: PrivateMarketplaceApi, query: any, sort: string, page: number = 0, pageSize: number = DEFAULT_PAGE_SIZE) {
    return (await privateApi.findProductsV3(JSON.stringify(query), sort, page, pageSize)).data as IPage<IProductDto>;
}

export async function loadMyComments(privateApi: PrivateMarketplaceApi, page: number = 0, pageSize: number = DEFAULT_PAGE_SIZE) {
    return (await privateApi.listMyComments(page, pageSize)).data as IPage<ICommentDto>
}

export enum MarketplaceSort {
    NONE,
    PRICE_DESCENDING,
    PRICE_ASCENDING,
    ORDER_COUNT_DESCENDING,
}

export const MarketplaceSorts = Object.keys(MarketplaceSort).map(key => MarketplaceSort[key as unknown as number]).filter(value => typeof value === 'string') as string[];