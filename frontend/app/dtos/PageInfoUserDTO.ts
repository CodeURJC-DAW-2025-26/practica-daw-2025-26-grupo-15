export interface PageInfoUserDTO {
    content: Array<{ name: string }>;
    page: {
        size: number;
        number: number;
        totalElements: number;
        totalPages: number;
    };
}