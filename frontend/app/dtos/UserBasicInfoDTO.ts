import type { ImageDto } from "./ImageDto";

export interface UserBasicInfoDTO {
    id: number;
    name: string;
    email: string;
    photo: ImageDto;
}