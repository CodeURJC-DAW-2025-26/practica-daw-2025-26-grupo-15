import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
	layout("routes/home.tsx", [
		index("routes/fyp.tsx"),
		route("login","routes/sign-in.tsx"),
		route("users/:id", "routes/profile.tsx", { id: "user-profile-by-id" }),
		route("users/me", "routes/profile.tsx", { id: "user-profile-me" }),
		route("lists/:id", "routes/list-view.tsx"),
		route("lists/new", "routes/new-list.tsx"),
		route("lists/edit/:id", "routes/edit-list.tsx"),

	]),
] satisfies RouteConfig;
