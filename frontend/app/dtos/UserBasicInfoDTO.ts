import type ImageDTO from "./ImageDto";


export default interface UserBasicInfoDTO {
    id: number;
    name: string;
    email: string;
    photo: ImageDTO | null;
}
