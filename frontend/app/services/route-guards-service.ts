import { redirect } from "react-router";
import { HttpError, reqIsLogged } from "./login-service";

export async function requireUser() {
    try{
        return await reqIsLogged();

    }catch(error){
        if (error instanceof HttpError && error.status === 404) {
            throw redirect("/login");
        }
        throw Error
    }
}

export async function requireRole(role: string) {
  const user = await requireUser();

  if (!user.roles.includes(role)) {
    throw Error("You don't have the necessary role to access this route. Please, don't try again.");
  }
  return user;
}


export async function optionalUser() {
  try {
    return await reqIsLogged();
  } catch (error) {
    if (error instanceof HttpError && error.status === 404) {
      return null;
    }
    throw error;
  }
}